/*
 * Copyright (c) 2014 Sequencing Analysis Support Core - Leiden University Medical Center
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nl.biopet.tools.validateannotation

import java.io.File

import nl.biopet.utils.ngs.fasta
import nl.biopet.utils.ngs.annotation.Feature
import nl.biopet.utils.tool.ToolCommand
import nl.biopet.tools.gtftorefflat.GtftoRefflat

import scala.io.Source

object ValidateAnnotation extends ToolCommand[Args] {
  def emptyArgs: Args = Args()
  def argsParser = new ArgsParser(this)
  def main(args: Array[String]): Unit = {
    val cmdArgs = cmdArrayToArgs(args)

    require(cmdArgs.refflatFile.nonEmpty || cmdArgs.gtfFiles.nonEmpty,
            "The tool require at least 1 annotations file, (refflat or gtf)")

    logger.info("Start")

    val dict = fasta.getCachedDict(cmdArgs.reference)

    try {

      val refflatLines =
        cmdArgs.refflatFile.map(Source.fromFile(_).getLines().toList.sorted)

      for (line <- refflatLines.getOrElse(Nil)) {
        val contig = line.split("\t")(2)
        require(
          dict.getSequence(contig) != null,
          s"Contig '$contig' found in refflat but not found on reference")
      }

      cmdArgs.gtfFiles.distinct.foreach { file =>
        refflatLines match {
          case Some(lines) =>
            val tempRefflat = File.createTempFile("temp.", ".refflat")
            tempRefflat.deleteOnExit()
            GtftoRefflat.gtfToRefflat(file,
                                      tempRefflat,
                                      Some(cmdArgs.reference))

            val tempRefflatLines =
              Source.fromFile(tempRefflat).getLines().toList.sorted
            for ((line1, line2) <- lines.zip(tempRefflatLines)) {
              require(line1 == line2,
                      "Refflat and gtf contain different information")
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

  def descriptionText: String =
    """
      |Validate annotation validates whether the annotation file is correct given a set of GTF files.
    """.stripMargin

  def manualText: String =
    """
      |
      |ValidateAnnotation requires the refflatfile and a
      |reference genome to check the annotation. A list
      |of gtf files can be optionally used for checking as well.
    """.stripMargin

  def exampleText: String =
    s"""
       |${example("-r",
                  "refflatFile",
                  "-g",
                  "gtfGile1",
                  "-g",
                  "gtfFile2",
                  "-g",
                  "gtfFile3",
                  "-R",
                  "reference.fasta")}
     """.stripMargin
}
