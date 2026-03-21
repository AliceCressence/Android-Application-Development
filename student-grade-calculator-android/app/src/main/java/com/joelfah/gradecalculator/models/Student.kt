package com.joelfah.gradecalculator.models

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val studentId: String,
    val name: String,
    val scores: List<Double>,
    val average: Double = 0.0,
    val grade: String = "",
    val gpa: Double = 0.0,
    val status: String = "",
) {
    val letterGrade: String get() = grade
    val isPass: Boolean get() = status == "PASS"
}
