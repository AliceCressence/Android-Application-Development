import exporters.ExcelExporter
import factory.ExportFormat
import factory.ExporterFactory
import io.DefaultFileManager
import io.ExcelReader
import logging.Application
import logging.ConsoleLogger
import services.GradeCalculator

/**
 * SE 3242 — Student Grade Calculator (v2 — Week 3 Edition)
 *
 * OOP concepts demonstrated (from lecturer's notes):
 * ─ ABSTRACT CLASS     : Student
 * ─ INHERITANCE        : UndergraduateStudent, GraduateStudent extend Student
 * ─ POLYMORPHISM       : describe(), studentType(), withComputedGrade()
 * ─ SEALED CLASS       : Grade (A, B+, B, C+, C, D+, D, F)
 * ─ INTERFACE          : ReportExporter, FileManager, Logger
 * ─ FACTORY METHOD     : ExporterFactory.create(format)
 * ─ CLASS DELEGATION   : Application delegates Logger via `by`
 * ─ SCOPE FUNCTIONS    : apply, let, also, with used throughout
 * ─ PROPERTY DELEGATION: by lazy in ExporterFactory
 * ─ NO LOOPS           : map, filter, forEachIndexed, joinToString everywhere
 *
 * Usage:
 *   java -jar app.jar --input input.xlsx --output results --format all
 */

data class CliArgs(
    val inputPath: String?,
    val outputBase: String?,
    val formats: List<ExportFormat>,
    val showHelp: Boolean
)

/** Parses args functionally — no while/for loops. */
fun parseArgs(args: Array<String>): CliArgs {
    val argMap = args.toList()
        .zipWithNext()
        .filter  { (k, _) -> k.startsWith("-") }
        .associate { (k, v) -> k to v }

    val formatStr = argMap["--format"] ?: argMap["-f"] ?: "all"
    val formats   = when (formatStr.lowercase()) {
        "all" -> ExportFormat.values().toList()
        else  -> listOfNotNull(ExporterFactory.fromString(formatStr))
    }

    return CliArgs(
        inputPath  = argMap["--input"]  ?: argMap["-i"],
        outputBase = argMap["--output"] ?: argMap["-o"],
        formats    = formats,
        showHelp   = "--help" in args || "-h" in args
    )
}

fun main(args: Array<String>) {

    // CLASS DELEGATION — Application delegates all log() calls to ConsoleLogger
    val app = Application(ConsoleLogger())
    app.start()

    // ── 1. Parse arguments ───────────────────────────────────────────────────
    val (inputPath, outputBase, formats, showHelp) = parseArgs(args)

    if (showHelp) {
        println("""
Student Grade Calculator v2 — Week 3 Edition
=============================================
Usage:
  java -jar app.jar --input <file.xlsx> --output <base_name> --format <format>

Flags:
  --input,  -i   Path to input .xlsx                      (required)
  --output, -o   Base name for output files               (required)
  --format, -f   excel | html | json | all                (default: all)
  --help,   -h   Show this help

Formats supported: ${ExporterFactory.supportedFormats()}
        """.trimIndent())
        return
    }

    if (inputPath == null || outputBase == null) {
        println("Error: --input and --output are required. Run with --help.")
        return
    }

    // ── 2. Read students ─────────────────────────────────────────────────────
    app.log("Reading from: $inputPath")
    val reader         = ExcelReader(inputPath)
    val students       = reader.read()
    val subjectHeaders = reader.readSubjectHeaders()
    app.log("Found ${students.size} students")

    // ── 3. Compute grades ────────────────────────────────────────────────────
    println("\n📊 Computing grades:")
    println("─".repeat(60))

    // SCOPE FUNCTION — `with` operates on calculator without repeating its name
    val enrichedStudents = with(GradeCalculator()) {
        calculateAll(students)
    }

    // ── 4. Describe each student (POLYMORPHISM) ──────────────────────────────
    println("\n📋 Student Details:")
    println("─".repeat(60))
    // describe() calls the correct subclass override at runtime
    enrichedStudents.forEach { println("  ${it.describe()}") }

    // ── 5. Export (FACTORY METHOD) ───────────────────────────────────────────
    val fileManager = DefaultFileManager()
    println("\n📤 Exporting reports:")
    println("─".repeat(60))

    // SCOPE FUNCTION — `let` transforms each format to its output path
    formats.forEach { format ->
        ExporterFactory.create(format).let { exporter ->
            val content    = exporter.export(enrichedStudents, subjectHeaders)
            val outputPath = "$outputBase.${exporter.fileExtension()}"
            fileManager.write(content, outputPath, exporter is ExcelExporter)
            app.log("${exporter.formatName()} → $outputPath")
            println("  ✅ ${exporter.formatName()} → $outputPath")
        }
    }

    // ── 6. Summary — uses sealed Grade's isPassing property ─────────────────
    // SCOPE FUNCTION — `apply` builds the summary block
    val summary = buildString {
        val total     = enrichedStudents.size
        val passCount = enrichedStudents.count { it.grade.isPassing }
        val failCount = total - passCount
        val ugCount   = enrichedStudents.count { it.studentType() == "Undergraduate" }
        val grCount   = total - ugCount

        appendLine("\n=== Student Grade Calculator — Summary ===")
        appendLine("Total students    : $total")
        appendLine("  Undergraduates  : $ugCount")
        appendLine("  Graduates       : $grCount")
        appendLine("Passed            : $passCount")
        appendLine("Failed            : $failCount")
        appendLine("Output base       : $outputBase")
    }
    println(summary)

    // Grade summary using sealed class — when is exhaustive over Grade
    println("📊 Grade Breakdown:")
    println("─".repeat(60))
    enrichedStudents.forEach { println("  ${it.summaryLine()}") }

    app.stop()
}