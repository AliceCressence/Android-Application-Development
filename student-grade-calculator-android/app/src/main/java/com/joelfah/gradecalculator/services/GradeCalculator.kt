package com.joelfah.gradecalculator.services

import com.joelfah.gradecalculator.models.Student
import kotlin.math.roundToInt

class GradeCalculator {

    fun computeAverage(scores: List<Double>): Double {
        if (scores.isEmpty()) return 0.0
        return scores.average()
    }

    fun assignGrade(average: Double): String = when {
        average >= 80 -> "A"
        average >= 70 -> "B+"
        average >= 60 -> "B"
        average >= 55 -> "C+"
        average >= 50 -> "C"
        average >= 45 -> "D+"
        average >= 40 -> "D"
        else          -> "F"
    }

    fun assignGpa(grade: String): Double = when (grade) {
        "A"  -> 4.0
        "B+" -> 3.5
        "B"  -> 3.0
        "C+" -> 2.5
        "C"  -> 2.0
        "D+" -> 1.5
        "D"  -> 1.0
        else -> 0.0
    }

    fun determineStatus(grade: String): String = if (grade == "F") "FAIL" else "PASS"

    fun calculate(student: Student): Student {
        val rawAvg = computeAverage(student.scores)
        val rounded = (rawAvg * 100).roundToInt() / 100.0
        val grade = assignGrade(rounded)
        return student.copy(
            average = rounded,
            grade = grade,
            gpa = assignGpa(grade),
            status = determineStatus(grade)
        )
    }

    fun calculateAll(students: List<Student>): List<Student> = students.map { calculate(it) }

    fun classAverage(students: List<Student>): Double {
        if (students.isEmpty()) return 0.0
        return students.map { it.average }.average()
    }
}
