package models

/**
 * Graduate student — concrete subclass of [Student].
 *
 * INHERITANCE : extends Student, inherits all properties and summaryLine()
 * POLYMORPHISM: overrides studentType(), describe(), withComputedGrade()
 * DATA CLASS  : auto-generates equals(), hashCode(), toString(), copy()
 *
 * @property thesisTitle Title of graduate thesis (defaults to "N/A").
 */
data class GraduateStudent(
    override val studentId: String,
    override val name: String,
    override val scores: List<Double>,
    override val average: Double = 0.0,
    override val grade: Grade = Grade.F,
    val thesisTitle: String = "N/A"
) : Student() {

    override fun studentType(): String = "Graduate"

    override fun describe(): String =
        "$name (Graduate — Thesis: \"$thesisTitle\") — " +
        "Avg: ${"%.2f".format(average)}, Grade: ${grade.letter}, " +
        "GPA: ${grade.gpa}, Status: ${grade.status()}"

    override fun withComputedGrade(average: Double, grade: Grade): Student =
        copy(average = average, grade = grade)
}