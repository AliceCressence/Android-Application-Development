package models

/**
 * Abstract base class representing a student.
 *
 * OOP concepts from the lecturer's notes (Slide 2):
 * ─ ABSTRACT CLASS   : cannot be instantiated directly
 * ─ INHERITANCE      : UndergraduateStudent and GraduateStudent extend this
 * ─ POLYMORPHISM     : studentType(), describe(), withComputedGrade()
 *                      dispatch to the correct subclass at runtime
 *
 * Uses [Grade] (sealed class) instead of plain String for type safety.
 */
abstract class Student {
    abstract val studentId: String
    abstract val name: String
    abstract val scores: List<Double>
    abstract val average: Double
    abstract val grade: Grade

    abstract fun studentType(): String
    abstract fun describe(): String
    abstract fun withComputedGrade(average: Double, grade: Grade): Student

    /**
     * Shared summary — uses scope function `let` to format average inline.
     * SCOPE FUNCTION (let): average.let { "%.2f".format(it) }
     */
    fun summaryLine(): String =
        "[${studentType()}] $studentId | $name | " +
        "Avg: ${average.let { "%.2f".format(it) }} | " +
        "Grade: ${grade.letter} | GPA: ${grade.gpa} | ${grade.status()}"
}