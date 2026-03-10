package models

/**
 * Represents a student with their scores and computed grade information.
 *
 * This is a Kotlin `data class`, which automatically provides:
 * - `toString()` — a readable string representation
 * - `copy()` — creates a modified copy (used by GradeCalculator to return enriched students)
 * - `equals()` / `hashCode()` — structural equality based on all properties
 *
 * @property studentId Unique identifier for the student (e.g. "S001").
 * @property name Full name of the student (e.g. "Alice Martin").
 * @property scores List of numeric scores for each subject.
 * @property average Arithmetic mean of all [scores]. Defaults to 0.0 until computed.
 * @property grade Letter grade derived from [average] (e.g. "A", "B+", "F").
 * @property gpa Grade Point Average on a 4.0 scale, derived from [average].
 * @property status Pass/fail status: "PASS" if grade is D or above, "FAIL" otherwise.
 */
data class Student(
    val studentId: String,
    val name: String,
    val scores: List<Double>,
    val average: Double = 0.0,
    val grade: String = "",
    val gpa: Double = 0.0,
    val status: String = ""
)
