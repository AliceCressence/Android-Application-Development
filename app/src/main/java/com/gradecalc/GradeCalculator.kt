package com.gradecalc.model

// BASE class — abstract Calculator
abstract class Calculator {
    protected val students = mutableListOf<Student>()

    // Add a student
    fun addStudent(student: Student) {
        students.add(student)
    }

    // Delete one by index
    fun deleteStudent(index: Int) {
        if (index in students.indices) students.removeAt(index)
    }

    // Delete ALL
    fun deleteAll() {
        students.clear()
    }

    fun getAllStudents(): List<Student> = students.toList()

    abstract fun calculate(): Double
}

// DERIVED class — inherits from Calculator
class GradeCalculator : Calculator() {

    // Lambda stored as property — grade letter function
    private val gradeLetterFn: (Double) -> String = { percentage ->
        when {
            percentage >= 90 -> "A+"
            percentage >= 80 -> "A"
            percentage >= 70 -> "B"
            percentage >= 60 -> "C"
            percentage >= 50 -> "D"
            else             -> "F"
        }
    }

    // Lambda predicate — pass or fail
    private val passFailFn: (Double) -> Boolean = { it >= 50.0 }

    // Override — calculates average of all valid scores
    override fun calculate(): Double {
        val validPercentages = students
            .mapNotNull { it.percentage }   // filter nulls (from lecturer's notes)
        return if (validPercentages.isEmpty()) 0.0
        else validPercentages.fold(0.0) { acc, d -> acc + d } / validPercentages.size
    }

    // GPA on 4.0 scale
    fun calculateGPA(): Double = when {
        calculate() >= 90 -> 4.0
        calculate() >= 80 -> 3.7
        calculate() >= 70 -> 3.3
        calculate() >= 60 -> 3.0
        calculate() >= 50 -> 2.0
        else              -> 0.0
    }

    // Uses lambda
    fun getGradeLetter(percentage: Double): String =
        gradeLetterFn(percentage)

    // Uses predicate lambda
    fun getPassFail(percentage: Double): String =
        if (passFailFn(percentage)) "PASS ✅" else "FAIL ❌"

    fun getOverallPassFail(): String = getPassFail(calculate())

    // Process students with null-safe handling (from notes)
    fun processStudents(): List<String> =
        students.map { student ->
            student.percentage?.let { pct ->
                "${student.name} scored ${"%.1f".format(pct)}% : " +
                        "Grade ${getGradeLetter(pct)} — ${getPassFail(pct)}"
            } ?: "No score for ${student.name}"  // Elvis operator
        }
}