package nl.biopet.tools.validateannotation

import nl.biopet.utils.test.tools.ToolTest
import org.testng.annotations.Test

class ValidateAnnotationTest extends ToolTest[Args] {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      ValidateAnnotation.main(Array())
    }
  }
}
