package nl.biopet.tools.validateannotation

import java.io.File

import nl.biopet.utils.tool.{AbstractOptParser, ToolCommand}

class ArgsParser(toolCommand: ToolCommand[Args])
    extends AbstractOptParser[Args](toolCommand) {
  opt[File]('r', "refflatFile") unbounded () maxOccurs 1 valueName "<file>" action {
    (x, c) =>
      c.copy(refflatFile = Some(x))
  } text "Refflat file to check"
  opt[File]('g', "gtfFile") unbounded () valueName "<file>" action { (x, c) =>
    c.copy(gtfFiles = x :: c.gtfFiles)
  } text "Gtf files to check"
  opt[File]('R', "reference") unbounded () required () maxOccurs 1 valueName "<file>" action {
    (x, c) =>
      c.copy(reference = x)
  } text "Reference fasta to check vcf file against"
  opt[Unit]("disableFail") unbounded () maxOccurs 1 valueName "<file>" action {
    (_, c) =>
      c.copy(failOnError = false)
  } text "Do not fail on error. The tool will still exit when encountering an error, but will do so with exit code 0"
}
