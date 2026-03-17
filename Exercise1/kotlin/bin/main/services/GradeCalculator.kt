package services

import models.Student

/**
 * Service responsible for computing grades, GPAs, and pass/fail status.
 *
 * Uses the following grading scale:
 *
 * | Grade | GPA | Min | Max |
 * |-------|-----|-----|-----|
 * | A     | 4.0 | 80  | 100 |
 * | B+    | 3.5 | 70  | 79  |
 * | B     | 3.0 | 60  | 69  |
 * | C+    | 2.5 | 55  | 59  |
 * | C     | 2.0 | 50  | 54  |
 * | D+    | 1.5 | 45  | 49  |
 * | D     | 1.0 | 40  | 44  |
 * | F     | 0.0 | 0   | 39  |
 */
class GradeCalculator {

    // -------------------------------------------------------------------------
    // Grading-scale boundary constants (no magic numbers in logic)
    // -------------------------------------------------------------------------

    companion object {
        /** Minimum average required for grade A. */
        const val MIN_A = 80.0
        /** Minimum average required for grade B+. */
        const val MIN_B_PLUS = 70.0
        /** Minimum average required for grade B. */
        const val MIN_B = 60.0
        /** Minimum average required for grade C+. */
        const val MIN_C_PLUS = 55.0
        /** Minimum average required for grade C. */
        const val MIN_C = 50.0
        /** Minimum average required for grade D+. */
        const val MIN_D_PLUS = 45.0
        /** Minimum average required for grade D (also the pass threshold). */
        const val MIN_D = 40.0
    }

    // -------------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------------

    /**
     * Computes the arithmetic mean of [scores].
     *
     * Returns `0.0` if the list is empty to avoid division-by-zero.
     */
    fun computeAverage(scores: List<Double>): Double {
        if (scores.isEmpty()) return 0.0

        // Sum all scores and divide by the number of subjects
        return scores.sum() / scores.size
    }

    /**
     * Returns the letter grade corresponding to the given [average].
     *
     * Uses Kotlin's `when` expression — a cleaner alternative to if/else chains
     * or Java's switch statement. The `when` block evaluates conditions top-down
     * and returns the result of the first matching branch.
     */
    fun assignGrade(average: Double): String = when {
        average >= MIN_A      -> "A"
        average >= MIN_B_PLUS -> "B+"
        average >= MIN_B      -> "B"
        average >= MIN_C_PLUS -> "C+"
        average >= MIN_C      -> "C"
        average >= MIN_D_PLUS -> "D+"
        average >= MIN_D      -> "D"
        else                  -> "F"
    }

    /**
     * Returns the GPA value corresponding to the given [average].
     *
     * Mirrors the same boundary logic as [assignGrade].
     */
    fun assignGpa(average: Double): Double = when {
        average >= MIN_A      -> 4.0
        average >= MIN_B_PLUS -> 3.5
        average >= MIN_B      -> 3.0
        average >= MIN_C_PLUS -> 2.5
        average >= MIN_C      -> 2.0
        average >= MIN_D_PLUS -> 1.5
        average >= MIN_D      -> 1.0
        else                  -> 0.0
    }

    /**
     * Returns `"PASS"` when [grade] is D or above, `"FAIL"` when grade is `"F"`.
     */
    fun determineStatus(grade: String): String =
        if (grade == "F") "FAIL" else "PASS"

    /**
     * Fills in average, grade, gpa, and status on a [student] and returns
     * an enriched copy.
     *
     * Uses Kotlin's `data class` `copy()` method to return a new immutable
     * instance with all computed fields set.
     */
    fun calculate(student: Student): Student {
        // Step 1 — compute the average of all subject scores
        val avg = computeAverage(student.scores)

        // Step 2 — determine the letter grade from the average
        val letterGrade = assignGrade(avg)

        // Step 3 — determine the GPA from the average
        val gpa = assignGpa(avg)

        // Step 4 — determine pass/fail from the letter grade
        val status = determineStatus(letterGrade)

        // Return an enriched copy of the student (original is unchanged)
        return student.copy(
            average = avg,
            grade = letterGrade,
            gpa = gpa,
            status = status
        )
    }

    /**
     * Applies [calculate] to every student in [students] and returns a new list.
     */
    fun calculateAll(students: List<Student>): List<Student> =
        students.map { calculate(it) }
}
