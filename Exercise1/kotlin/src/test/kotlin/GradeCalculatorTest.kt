import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import services.GradeCalculator

/**
 * Unit tests for [GradeCalculator].
 *
 * Tests every boundary value in the grading scale plus edge cases for
 * [computeAverage] and [determineStatus].
 */
class GradeCalculatorTest {

    private lateinit var calculator: GradeCalculator

    @BeforeEach
    fun setUp() {
        calculator = GradeCalculator()
    }

    // =========================================================================
    // Tests for computeAverage
    // =========================================================================

    @Test
    fun `empty list returns 0`() {
        assertEquals(0.0, calculator.computeAverage(emptyList()))
    }

    @Test
    fun `single score returns that score`() {
        assertEquals(75.0, calculator.computeAverage(listOf(75.0)))
    }

    @Test
    fun `multiple scores returns correct mean`() {
        // (80 + 60 + 70) / 3 = 70
        assertEquals(70.0, calculator.computeAverage(listOf(80.0, 60.0, 70.0)))
    }

    @Test
    fun `handles decimal averages`() {
        // (85 + 90 + 78 + 88) / 4 = 85.25
        assertEquals(85.25, calculator.computeAverage(listOf(85.0, 90.0, 78.0, 88.0)))
    }

    // =========================================================================
    // Tests for assignGrade — every boundary value
    // =========================================================================

    @Test fun `100 maps to A`()  = assertEquals("A",  calculator.assignGrade(100.0))
    @Test fun `80 maps to A`()   = assertEquals("A",  calculator.assignGrade(80.0))
    @Test fun `79 maps to B+`()  = assertEquals("B+", calculator.assignGrade(79.0))
    @Test fun `70 maps to B+`()  = assertEquals("B+", calculator.assignGrade(70.0))
    @Test fun `69 maps to B`()   = assertEquals("B",  calculator.assignGrade(69.0))
    @Test fun `60 maps to B`()   = assertEquals("B",  calculator.assignGrade(60.0))
    @Test fun `59 maps to C+`()  = assertEquals("C+", calculator.assignGrade(59.0))
    @Test fun `55 maps to C+`()  = assertEquals("C+", calculator.assignGrade(55.0))
    @Test fun `54 maps to C`()   = assertEquals("C",  calculator.assignGrade(54.0))
    @Test fun `50 maps to C`()   = assertEquals("C",  calculator.assignGrade(50.0))
    @Test fun `49 maps to D+`()  = assertEquals("D+", calculator.assignGrade(49.0))
    @Test fun `45 maps to D+`()  = assertEquals("D+", calculator.assignGrade(45.0))
    @Test fun `44 maps to D`()   = assertEquals("D",  calculator.assignGrade(44.0))
    @Test fun `40 maps to D`()   = assertEquals("D",  calculator.assignGrade(40.0))
    @Test fun `39 maps to F`()   = assertEquals("F",  calculator.assignGrade(39.0))
    @Test fun `0 maps to F`()    = assertEquals("F",  calculator.assignGrade(0.0))

    // =========================================================================
    // Tests for assignGpa — mirrors assignGrade boundaries
    // =========================================================================

    @Test fun `gpa 100 is 4_0`() = assertEquals(4.0, calculator.assignGpa(100.0))
    @Test fun `gpa 80 is 4_0`()  = assertEquals(4.0, calculator.assignGpa(80.0))
    @Test fun `gpa 79 is 3_5`()  = assertEquals(3.5, calculator.assignGpa(79.0))
    @Test fun `gpa 70 is 3_5`()  = assertEquals(3.5, calculator.assignGpa(70.0))
    @Test fun `gpa 69 is 3_0`()  = assertEquals(3.0, calculator.assignGpa(69.0))
    @Test fun `gpa 60 is 3_0`()  = assertEquals(3.0, calculator.assignGpa(60.0))
    @Test fun `gpa 59 is 2_5`()  = assertEquals(2.5, calculator.assignGpa(59.0))
    @Test fun `gpa 55 is 2_5`()  = assertEquals(2.5, calculator.assignGpa(55.0))
    @Test fun `gpa 54 is 2_0`()  = assertEquals(2.0, calculator.assignGpa(54.0))
    @Test fun `gpa 50 is 2_0`()  = assertEquals(2.0, calculator.assignGpa(50.0))
    @Test fun `gpa 49 is 1_5`()  = assertEquals(1.5, calculator.assignGpa(49.0))
    @Test fun `gpa 45 is 1_5`()  = assertEquals(1.5, calculator.assignGpa(45.0))
    @Test fun `gpa 44 is 1_0`()  = assertEquals(1.0, calculator.assignGpa(44.0))
    @Test fun `gpa 40 is 1_0`()  = assertEquals(1.0, calculator.assignGpa(40.0))
    @Test fun `gpa 39 is 0_0`()  = assertEquals(0.0, calculator.assignGpa(39.0))
    @Test fun `gpa 0 is 0_0`()   = assertEquals(0.0, calculator.assignGpa(0.0))

    // =========================================================================
    // Tests for determineStatus
    // =========================================================================

    @Test fun `F means FAIL`()   = assertEquals("FAIL", calculator.determineStatus("F"))
    @Test fun `A means PASS`()   = assertEquals("PASS", calculator.determineStatus("A"))
    @Test fun `B+ means PASS`()  = assertEquals("PASS", calculator.determineStatus("B+"))
    @Test fun `B means PASS`()   = assertEquals("PASS", calculator.determineStatus("B"))
    @Test fun `C+ means PASS`()  = assertEquals("PASS", calculator.determineStatus("C+"))
    @Test fun `C means PASS`()   = assertEquals("PASS", calculator.determineStatus("C"))
    @Test fun `D+ means PASS`()  = assertEquals("PASS", calculator.determineStatus("D+"))
    @Test fun `D means PASS`()   = assertEquals("PASS", calculator.determineStatus("D"))
}
