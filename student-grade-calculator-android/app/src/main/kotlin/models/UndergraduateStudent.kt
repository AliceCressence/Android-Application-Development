package models

/**
 * Undergraduate student — concrete subclass of [Student].
 *
 * INHERITANCE : extends Student, inherits all properties and summaryLine()
 * POLYMORPHISM: overrides studentType(), describe(), withComputedGrade()
 * DATA CLASS  : auto-generates equals(), hashCode(), toString(), copy()
 *
 * SCOPE FUNCTION — apply used in describe() to build the string cleanly.
 *
 * @property yearOfStudy 1=Freshman, 2=Sophomore, 3=Junior, 4=Senior.
 */
data class UndergraduateStudent(
    override val studentId: String,
    override val name: String,
    override val scores: List<Double>,
    override val average: Double = 0.0,
    override val grade: Grade = Grade.F,
    val yearOfStudy: Int = 1
) : Student() {

    override fun studentType(): String = "Undergraduate"

    override fun describe(): String =
        "$name (Year $yearOfStudy Undergraduate) — " +
        "Avg: ${"%.2f".format(average)}, Grade: ${grade.letter}, " +
        "GPA: ${grade.gpa}, Status: ${grade.status()}"

    override fun withComputedGrade(average: Double, grade: Grade): Student =
        copy(average = average, grade = grade)
}