package com.joelfah.gradecalculator.models

import kotlinx.serialization.Serializable

@Serializable
data class GradeSession(
    val id: Long? = null,
    val fileName: String,
    val processedAt: String,
    val totalStudents: Int,
    val passCount: Int,
    val failCount: Int,
    val classAverage: Double,
    val students: List<Student>,
    val subjectHeaders: List<String>,
)
