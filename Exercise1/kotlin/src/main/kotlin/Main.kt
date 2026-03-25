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

/**
 * Holds the parsed command-line arguments.
 *
 * @property inputPath  Path to the input `.xlsx` file, or null if not provided.
 * @property outputPath Path to the output `.xlsx` file, or null if not provided.
 * @property showHelp   True when --help / -h is present in the arguments.
 */
data class CliArgs(
    val inputPath: String?,
    val outputPath: String?,
    val showHelp: Boolean
)

/**
 * Parses [args] into a [CliArgs] without any loops.
 *
 * Strategy:
 *  1. [zipWithNext] pairs every argument with the one that follows it, e.g.
 *     ["--input", "data.xlsx", "--output", "out.xlsx"]
 *     → [("--input","data.xlsx"), ("data.xlsx","--output"), ("--output","out.xlsx")]
 *  2. [filter] keeps only pairs whose first element is a flag (starts with "-").
 *     → [("--input","data.xlsx"), ("--output","out.xlsx")]
 *  3. [associate] turns those pairs into a Map<String, String>.
 *     → {"--input" -> "data.xlsx", "--output" -> "out.xlsx"}
 *  4. Flags that take no value (--help) are detected with [contains].
 *
 * CORRECTION: The original used a `while` loop with a manual index counter `i`,
 * which violates the no-loops requirement. This functional approach replaces it
 * entirely.
 */
fun parseArgs(args: Array<String>): CliArgs {
    // Build a flag→value map from consecutive argument pairs
    val argMap: Map<String, String> = args.toList()
        .zipWithNext()                              // pair every element with its neighbour
        .filter  { (key, _) -> key.startsWith("-") } // keep only flag-value pairs
        .associate { (key, value) -> key to value }  // turn into a map

    return CliArgs(
        inputPath  = argMap["--input"]  ?: argMap["-i"],
        outputPath = argMap["--output"] ?: argMap["-o"],
        showHelp   = "--help" in args   || "-h" in args
    )
}

fun main(args: Array<String>) {

    // ── 1. Parse command-line arguments ─────────────────────────────────────
    val (inputPath, outputPath, showHelp) = parseArgs(args)

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
    val total     = enrichedStudents.size
    val passCount = enrichedStudents.count { it.status == "PASS" }
    val failCount = total - passCount

    println("=== Student Grade Calculator — Summary ===")
    println("Total students : $total")
    println("Passed         : $passCount")
    println("Failed         : $failCount")
    println("Output written : $outputPath")
}