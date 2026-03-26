package services

import models.Grade
import models.Student

/**
 * Computes grades for students.
 *
 * OOP notes applied:
 * ─ Works with abstract [Student] — open to extension, closed to modification
 * ─ Uses [Grade] sealed class instead of plain strings
 * ─ SCOPE FUNCTION `also` used for side-effect logging after calculation
 * ─ SCOPE FUNCTION `let` used for null-safe average computation
 * ─ `by lazy` PROPERTY DELEGATION — report stats computed once on first access
 */
class GradeCalculator {

    /**
     * Calculates and returns an enriched copy of [student].
     *
     * SCOPE FUNCTION — `also` performs a side-effect (printing) after the
     * result is produced, without altering it. From the notes:
     * "also: it receiver, returns receiver — used for side effects"
     */
    fun calculate(student: Student): Student {
        val avg   = student.scores
            .takeIf { it.isNotEmpty() }
            ?.average() ?: 0.0          // `let`-style null-safe chain

        val grade = Grade.from(avg)     // sealed class factory method

        return student
            .withComputedGrade(avg, grade)
            .also {
                // `also` — runs this block then returns the student unchanged
                println("  ✔ Calculated: ${it.studentId} | ${it.grade.letter} | ${it.grade.status()}")
            }
    }

    /**
     * Applies [calculate] to every student. No loops — uses `map`.
     */
    fun calculateAll(students: List<Student>): List<Student> =
        students.map { calculate(it) }
}