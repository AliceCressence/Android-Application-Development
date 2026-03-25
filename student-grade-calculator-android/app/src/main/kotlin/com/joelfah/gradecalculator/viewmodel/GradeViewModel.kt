package com.joelfah.gradecalculator.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ExcelReader
import exporters.ExcelExporter
import exporters.HtmlExporter
import exporters.JsonExporter
import exporters.ReportExporter
import factory.ExportFormat
import factory.ExporterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import models.Grade
import models.GraduateStudent
import models.Student
import models.UndergraduateStudent
import services.GradeCalculator
import java.io.File

/**
 * ViewModel — shared state for all 4 screens.
 *
 * OOP concepts used:
 * ─ INHERITANCE    : works with abstract Student type
 * ─ POLYMORPHISM   : studentType(), describe() dispatch to correct subclass
 * ─ FACTORY METHOD : ExporterFactory.create(format) returns right exporter
 * ─ INTERFACE      : ReportExporter used as type, not concrete classes
 * ─ SEALED CLASS   : Grade for type-safe grade representation
 * ─ NO LOOPS       : map, filter, count throughout
 */
class GradeViewModel : ViewModel() {

    private val calculator = GradeCalculator()
    private val excelReader = ExcelReader()

    // ── UI State ──────────────────────────────────────────────────────────────

    /** List of students with computed grades. */
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    /** Subject names entered by the user. */
    private val _subjects = MutableStateFlow<List<String>>(emptyList())
    val subjects: StateFlow<List<String>> = _subjects.asStateFlow()

    /** Export status message shown on the Export screen. */
    private val _exportStatus = MutableStateFlow<Map<String, String>>(emptyMap())
    val exportStatus: StateFlow<Map<String, String>> = _exportStatus.asStateFlow()

    /** Loading indicator. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ── Input Screen actions ──────────────────────────────────────────────────

    /**
     * Called from InputScreen when user submits student data.
     *
     * POLYMORPHISM: creates the right Student subclass based on [isGraduate],
     * then calls calculate() which works on the abstract Student type.
     */
    fun addAndCalculateStudents(
        rawStudents: List<Triple<String, String, Boolean>>, // id, name, isGraduate
        subjectNames: List<String>,
        scoreMap: Map<String, List<Double>>                 // studentId -> scores
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _subjects.value  = subjectNames

            // POLYMORPHISM: create right subclass based on isGraduate flag
            val students = rawStudents.map { (id, name, isGraduate) ->
                val scores = scoreMap[id] ?: emptyList()
                if (isGraduate)
                    GraduateStudent(studentId = id, name = name, scores = scores)
                else
                    UndergraduateStudent(studentId = id, name = name, scores = scores)
            }

            // calculator.calculateAll works on abstract Student — no type checking
            _students.value  = calculator.calculateAll(students)
            _isLoading.value = false
        }
    }

    /**
     * Import and calculate grades from an Excel file.
     *
     * This function:
     * 1. Reads the Excel file from Uri
     * 2. Parses students (ID, Name, isGraduate, scores)
     * 3. Builds subjects list, rawStudents list, and scoreMap
     * 4. Calls addAndCalculateStudents to process the data
     */
    fun importAndCalculateFromExcel(uri: Uri, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Read Excel file and parse data
                val (rawStudents, subjectNames, scoreMap) = excelReader.readExcelFile(uri, context)

                // Use existing method to calculate grades
                addAndCalculateStudents(rawStudents, subjectNames, scoreMap)

                _exportStatus.value = _exportStatus.value.toMutableMap().also {
                    it["import"] = "✅ Successfully imported from Excel"
                }
            } catch (e: Exception) {
                _exportStatus.value = _exportStatus.value.toMutableMap().also {
                    it["import"] = "❌ Import failed: ${e.message}"
                }
                throw e
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Export functions ─────────────────────────────────────────────────────

    /**
     * Export results to Excel format.
     * Convenience method for the InputScreen.
     */
    fun exportToExcel(context: Context) {
        exportReport(ExportFormat.EXCEL, context)
    }

    /**
     * Export results to HTML format.
     * Convenience method for the InputScreen.
     */
    fun exportToHtml(context: Context) {
        exportReport(ExportFormat.HTML, context)
    }

    /**
     * Export results to JSON format.
     * Convenience method for the InputScreen.
     */
    fun exportToJson(context: Context) {
        exportReport(ExportFormat.JSON, context)
    }

    // ── Export Screen actions ─────────────────────────────────────────────────

    /**
     * Exports results in the requested [format] using the FACTORY METHOD.
     *
     * FACTORY METHOD: ExporterFactory.create(format) returns the right
     * ReportExporter without Main ever knowing which class backs it.
     */
    fun exportReport(format: ExportFormat, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val exporter = ExporterFactory.create(format)
                val content = exporter.export(_students.value, _subjects.value)
                val fileName = "results_${System.currentTimeMillis()}.${exporter.fileExtension()}"
                val outputFile = File(context.getExternalFilesDir(null), fileName)

                // Handle different content types properly
                when (exporter) {
                    is ExcelExporter -> {
                        // Content is ByteArray for Excel
                        outputFile.writeBytes(content as ByteArray)
                    }
                    else -> {
                        // Content is String for other formats
                        outputFile.writeText(content as String)
                    }
                }

                // After saving, open/share the file
                openFile(context, outputFile)

                _exportStatus.value = _exportStatus.value.toMutableMap().also {
                    it[format.name] = "✅ Saved to: ${outputFile.absolutePath}"
                }
            } catch (e: Exception) {
                _exportStatus.value = _exportStatus.value.toMutableMap().also {
                    it[format.name] = "❌ Export failed: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Summary helpers (used by ResultsScreen) ───────────────────────────────

    /** Total number of students. */
    fun totalCount(): Int = _students.value.size

    /** Pass count — uses Grade sealed class isPassing property. */
    fun passCount(): Int = _students.value.count { it.grade.isPassing }

    /** Fail count. */
    fun failCount(): Int = totalCount() - passCount()

    /** Undergraduate count — POLYMORPHISM: studentType() dispatches correctly. */
    fun ugCount(): Int = _students.value.count { it.studentType() == "Undergraduate" }

    /** Graduate count. */
    fun grCount(): Int = totalCount() - ugCount()

    /** Clear all data */
    fun clearData() {
        viewModelScope.launch {
            _students.value = emptyList()
            _subjects.value = emptyList()
            _exportStatus.value = emptyMap()
        }
    }

    private fun openFile(context: android.content.Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Open Excel File"))
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                context,
                "No app found to open Excel files. Please install Microsoft Excel or Google Sheets.",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }}
}