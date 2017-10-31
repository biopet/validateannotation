package nl.biopet.tools.validateannotation

import java.io.File

import nl.biopet.utils.ngs.fasta
import nl.biopet.utils.ngs.annotation.Feature
import nl.biopet.utils.tool.ToolCommand
import nl.biopet.tools.gtftorefflat.GtftoRefflat

import scala.io.Source

object ValidateAnnotation extends ToolCommand[Args] {
  def main(args: Array[String]): Unit = {
    val parser = new ArgsParser(toolName)
    val cmdArgs =
      parser.parse(args, Args()).getOrElse(throw new IllegalArgumentException)

    logger.info("Start")

    val dict = fasta.getCachedDict(cmdArgs.reference)

    try {

      val refflatLines = cmdArgs.refflatFile.map(Source.fromFile(_).getLines().toList.sorted)

      for (line <- refflatLines.getOrElse(Nil)) {
        val contig = line.split("\t")(2)
        require(dict.getSequence(contig) != null,
          s"Contig '$contig' found in refflat but not found on reference")
      }

      cmdArgs.gtfFiles.distinct.foreach { file =>
        refflatLines match {
          case Some(lines) =>
            val tempRefflat = File.createTempFile("temp.", ".refflat")
            tempRefflat.deleteOnExit()
            GtftoRefflat.gtfToRefflat(file, tempRefflat, Some(cmdArgs.reference))

            val tempRefflatLines = Source.fromFile(tempRefflat).getLines().toList.sorted
            for ((line1, line2) <- lines.zip(tempRefflatLines)) {
              require(line1 == line2, "Refflat and gtf contain different information")
            }
          case _ =>
            Source
              .fromFile(file)
              .getLines()
              .filter(!_.startsWith("#"))
              .map(Feature.fromLine)
              .foreach { feature =>
                require(
                  dict.getSequence(feature.contig) != null,
                  s"Contig '${feature.contig}' found in gtf/gff but not found on reference: $file")
              }
        }
      }
    } catch {
      case e: IllegalArgumentException =>
        if (cmdArgs.failOnError) throw e
        else logger.error(e.getMessage)
    }

    logger.info("Done")
  }
}
