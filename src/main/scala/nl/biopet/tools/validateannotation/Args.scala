package nl.biopet.tools.validateannotation

import java.io.File

case class Args(refflatFile: Option[File] = None,
                reference: File = null,
                failOnError: Boolean = true,
                gtfFiles: List[File] = Nil)
