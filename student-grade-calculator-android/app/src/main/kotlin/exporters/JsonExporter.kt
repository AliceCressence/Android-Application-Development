package exporters

import models.Student

/**
 * Exports student results as a JSON array.
 *
 * POLYMORPHISM: Implements [ReportExporter]. Identical interface to
 * [HtmlExporter] and [ExcelExporter] — only the output format differs.
 *
 * Produces standards-compliant JSON without any external library.
 * No loops — uses [joinToString] and [map] throughout.
 */
class JsonExporter : ReportExporter {

    override fun fileExtension(): String = "json"
    override fun formatName(): String    = "JSON"

    /**
     * Produces a JSON object with:
     * - "summary": pass/fail counts
     * - "students": array of student objects
     *
     * Example output:
     * {
     *   "summary": { "total": 5, "passed": 4, "failed": 1 },
     *   "students": [ { "studentId": "S001", "name": "Alice", ... }, ... ]
     * }
     */
    override fun export(students: List<Student>, subjectHeaders: List<String>): String {
        val passCount = students.count { it.grade.isPassing }
        val failCount = students.size - passCount

        // Map each student to a JSON object string — no loop
        val studentJsonArray = students
            .map  { s -> studentToJson(s, subjectHeaders) }
            .joinToString(",\n    ")

        return """{
  "summary": {
    "total": ${students.size},
    "passed": $passCount,
    "failed": $failCount
  },
  "students": [
    $studentJsonArray
  ]
}"""
    }

    /**
     * Converts a single [student] to a JSON object string.
     *
     * The scores are mapped to key-value pairs using the [subjectHeaders]
     * as keys — no loop, uses [zip] + [joinToString].
     */
    private fun studentToJson(student: Student, subjectHeaders: List<String>): String {
        // Zip subject names with their scores → "Math": 85, "Science": 90, ...
        val scoreFields = subjectHeaders
            .zip(student.scores)
            .joinToString(", ") { (subject, score) ->
                "\"$subject\": ${score.toInt()}"
            }

        return """{
      "studentId": "${student.studentId}",
      "name": "${student.name}",
      "type": "${student.studentType()}",
      "scores": { $scoreFields },
      "average": ${"%.2f".format(student.average)},
      "grade": "${student.grade.letter}",
      "gpa": ${student.grade.gpa},
      "status": "${student.grade.status()}"
    }"""
    }
}