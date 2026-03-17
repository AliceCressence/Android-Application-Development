package com.gradecalc.model

// Data class from lecturer's notes — nullable score
data class Student(
    val name: String,
    val score: Double?,      // nullable — score might be missing
    val maxScore: Double = 100.0
) {
    // Computed percentage — safe call with Elvis
    val percentage: Double?
        get() = score?.let { (it / maxScore) * 100 }
}