package nl.biopet.tools.validateannotation

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

class ValidateAnnotationTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      ValidateAnnotation.main(Array())
    }
  }
}
