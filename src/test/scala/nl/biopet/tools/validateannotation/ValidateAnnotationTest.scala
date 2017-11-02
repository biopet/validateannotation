package nl.biopet.tools.validateannotation

import nl.biopet.utils.test.tools.ToolTest
import org.testng.annotations.Test

class ValidateAnnotationTest extends ToolTest[Args] {
  def toolCommand: ValidateAnnotation.type = ValidateAnnotation
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      ValidateAnnotation.main(Array())
    }
  }
}
