package exporters

import models.Student

/**
 * Exports student results as a styled HTML page.
 *
 * POLYMORPHISM: Implements [ReportExporter]. When the factory returns this
 * class as a [ReportExporter], callers use it identically to Excel or JSON
 * exporters — only the output format differs.
 *
 * No loops — uses [joinToString] and [map] to build all table rows.
 */
class HtmlExporter : ReportExporter {

    override fun fileExtension(): String = "html"
    override fun formatName(): String    = "HTML"

    /**
     * Produces a complete, self-contained HTML document with:
     * - A styled header showing pass/fail stats
     * - A colour-coded table (green = PASS, red = FAIL)
     * - Student type badge (Undergraduate / Graduate)
     */
    override fun export(students: List<Student>, subjectHeaders: List<String>): String {
        val passCount = students.count { it.status == "PASS" }
        val failCount = students.size - passCount

        // Build one <th> per subject header — no loop, uses joinToString
        val subjectHeaderCells = subjectHeaders
            .joinToString("") { "<th>$it</th>" }

        // Build one <tr> per student — map produces list, joinToString flattens
        val rows = students
            .map  { s -> buildRow(s, subjectHeaders.size) }
            .joinToString("\n")

        return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Student Grade Report</title>
  <style>
    body       { font-family: Arial, sans-serif; margin: 32px; background:#f4f6f9; }
    h1         { color: #2c3e50; }
    .stats     { display:flex; gap:24px; margin-bottom:24px; }
    .card      { background:#fff; border-radius:8px; padding:16px 28px;
                 box-shadow:0 2px 6px rgba(0,0,0,.1); text-align:center; }
    .card h2   { margin:0; font-size:2rem; }
    .card p    { margin:4px 0 0; color:#666; font-size:.9rem; }
    .pass      { color:#27ae60; }
    .fail      { color:#e74c3c; }
    table      { width:100%; border-collapse:collapse; background:#fff;
                 border-radius:8px; overflow:hidden;
                 box-shadow:0 2px 6px rgba(0,0,0,.1); }
    th         { background:#2c3e50; color:#fff; padding:10px 14px;
                 text-align:left; font-size:.85rem; }
    td         { padding:9px 14px; border-bottom:1px solid #eee; font-size:.9rem; }
    tr:last-child td { border-bottom:none; }
    tr.pass-row td   { background:#f0fff4; }
    tr.fail-row td   { background:#fff5f5; }
    .badge     { display:inline-block; padding:2px 8px; border-radius:12px;
                 font-size:.75rem; font-weight:bold; }
    .ug        { background:#dbeafe; color:#1d4ed8; }
    .gr        { background:#ede9fe; color:#6d28d9; }
    .status-pass { color:#27ae60; font-weight:bold; }
    .status-fail { color:#e74c3c; font-weight:bold; }
  </style>
</head>
<body>
  <h1>📊 Student Grade Report</h1>

  <div class="stats">
    <div class="card"><h2>${students.size}</h2><p>Total Students</p></div>
    <div class="card"><h2 class="pass">$passCount</h2><p>Passed</p></div>
    <div class="card"><h2 class="fail">$failCount</h2><p>Failed</p></div>
  </div>

  <table>
    <thead>
      <tr>
        <th>ID</th><th>Name</th><th>Type</th>
        $subjectHeaderCells
        <th>Average</th><th>Grade</th><th>GPA</th><th>Status</th>
      </tr>
    </thead>
    <tbody>
$rows
    </tbody>
  </table>
</body>
</html>""".trimIndent()
    }

    /** Builds a single <tr> for [student]. No loop — uses joinToString on scores. */
    private fun buildRow(student: Student, subjectCount: Int): String {
        val rowClass = if (student.grade.status() == "PASS") "pass-row" else "fail-row"
        val badge    = if (student.studentType() == "Undergraduate")
            "<span class=\"badge ug\">UG</span>"
        else
            "<span class=\"badge gr\">GR</span>"
        val statusClass = if (student.grade.status() == "PASS") "status-pass" else "status-fail"

        val scoreCells = student.scores
            .joinToString("") { "<td>${it.toInt()}</td>" }

        return """      <tr class="$rowClass">
        <td>${student.studentId}</td>
        <td>${student.name}</td>
        <td>$badge</td>
        $scoreCells
        <td>${"%.2f".format(student.average)}</td>
        <td>${student.grade.letter}</td>
        <td>${student.grade.gpa}</td>
        <td><span class="$statusClass">${student.grade.status()}</span></td>
      </tr>"""
    }
}