import io.ExcelReader
import io.ExcelWriter
import services.GradeCalculator

/**
 * Entry point for the Student Grade Calculator CLI (Kotlin edition).
 *
 * Usage:
 *   java -jar app.jar --input <path> --output <path>
 *
 * Flags:
 *   --input   Path to the input `.xlsx` file (required)
 *   --output  Path for the output `.xlsx` file (required)
 *   --help    Show usage information
 */
fun main(args: Array<String>) {

    // ── 1. Parse command-line arguments ─────────────────────────────────────
    var inputPath: String? = null
    var outputPath: String? = null
    var showHelp = false

    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--input", "-i"  -> {
                // The next argument is the input file path
                inputPath = args.getOrNull(i + 1)
                i += 2
            }
            "--output", "-o" -> {
                // The next argument is the output file path
                outputPath = args.getOrNull(i + 1)
                i += 2
            }
            "--help", "-h"   -> {
                showHelp = true
                i++
            }
            else -> i++
        }
    }

    // ── 2. Handle --help ────────────────────────────────────────────────────
    if (showHelp) {
        println("Student Grade Calculator — Kotlin edition")
        println()
        println("Usage:")
        println("  java -jar app.jar --input <file.xlsx> --output <file.xlsx>")
        println()
        println("Flags:")
        println("  --input, -i   Path to the input .xlsx file (required)")
        println("  --output, -o  Path for the output .xlsx file (required)")
        println("  --help, -h    Show this help message")
        return
    }

    // ── 3. Validate required arguments ──────────────────────────────────────
    if (inputPath == null || outputPath == null) {
        println("Error: --input and --output are required.")
        println("Run with --help for usage information.")
        return
    }

    // ── 4. Read students from the input Excel file ──────────────────────────
    val reader = ExcelReader(inputPath)
    val students = reader.read()
    val subjectHeaders = reader.readSubjectHeaders()

    // ── 5. Calculate grades for all students ────────────────────────────────
    val calculator = GradeCalculator()
    val enrichedStudents = calculator.calculateAll(students)

    // ── 6. Write enriched data to the output Excel file ─────────────────────
    val writer = ExcelWriter(outputPath)
    writer.write(enrichedStudents, subjectHeaders)

    // ── 7. Print a summary to the console ───────────────────────────────────
    val total = enrichedStudents.size
    val passCount = enrichedStudents.count { it.status == "PASS" }
    val failCount = total - passCount

    println("=== Student Grade Calculator — Summary ===")
    println("Total students : $total")
    println("Passed         : $passCount")
    println("Failed         : $failCount")
    println("Output written : $outputPath")
}
