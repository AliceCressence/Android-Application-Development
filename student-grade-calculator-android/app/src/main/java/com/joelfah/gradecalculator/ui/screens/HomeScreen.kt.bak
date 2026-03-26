package com.joelfah.gradecalculator.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.joelfah.gradecalculator.data.HistoryRepository
import com.joelfah.gradecalculator.models.GradeSession
import com.joelfah.gradecalculator.models.Student
import com.joelfah.gradecalculator.services.ExcelService
import com.joelfah.gradecalculator.services.GradeCalculator
import com.joelfah.gradecalculator.services.PdfService
import com.joelfah.gradecalculator.ui.components.EmptyState
import com.joelfah.gradecalculator.ui.components.ExportButtons
import com.joelfah.gradecalculator.ui.components.FilePickerCard
import com.joelfah.gradecalculator.ui.components.ResultsTable
import com.joelfah.gradecalculator.ui.components.StatSummaryRow
import com.joelfah.gradecalculator.ui.theme.AppSpacing
import com.joelfah.gradecalculator.ui.theme.AppTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: HistoryRepository,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var headers by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Services
    val excelService = remember { ExcelService(context.contentResolver) }
    val pdfService = remember { PdfService() }
    val gradeCalculator = remember { GradeCalculator() }

    // Logic to process file
    fun processFile(uri: Uri) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Read Background
                val (rawStudents, rawHeaders) = withContext(Dispatchers.IO) {
                    excelService.readFromUri(uri)
                }
                
                // Calculate
                val calculatedStudents = rawStudents.map { student ->
                    gradeCalculator.calculate(student)
                }
                
                students = calculatedStudents
                headers = rawHeaders

                // Save to History
                if (students.isNotEmpty()) {
                    val passCount = students.count { it.isPass }
                    val failCount = students.count { !it.isPass }
                    val avg = if (students.isNotEmpty()) students.map { it.average }.average() else 0.0
                    
                    val session = GradeSession(
                        fileName = "Imported File", // Or get real filename via content resolver query
                        processedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        totalStudents = students.size,
                        passCount = passCount,
                        failCount = failCount,
                        classAverage = avg,
                        students = students,
                        subjectHeaders = headers
                    )
                    withContext(Dispatchers.IO) {
                        repository.saveSession(session)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Error: ${e.message}"
                Toast.makeText(context, "Error processing file", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> 
            uri?.let { processFile(it) }
        }
    )

    // Export Logic
    val saveExcelLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        onResult = { uri ->
            uri?.let {
                scope.launch(Dispatchers.IO) {
                    try {
                        excelService.writeToUri(it, students, headers)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Exported Successfully", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Export Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    )

    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = { uri ->
            uri?.let {
                scope.launch(Dispatchers.IO) {
                    try {
                        val pdfBytes = pdfService.generateReport(students, headers, "Student Grades")
                        context.contentResolver.openOutputStream(it)?.use { os ->
                            os.write(pdfBytes)
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "PDF Report Saved", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "PDF Export Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grade Calculator", style = AppTypography.headlineMedium) },
                actions = {
                    Button(onClick = onNavigateToHistory) {
                        Text("History")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(AppSpacing.md)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                FilePickerCard(onClick = { 
                    pickFileLauncher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"))
                })
                
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (students.isEmpty()) {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
                    } else {
                        EmptyState("No data to show. Select a file.")
                    }
                } else {
                    // Summary
                    val passCount = students.count { it.isPass }
                    val failCount = students.count { !it.isPass }
                    val avg = students.map { it.average }.average()
                    
                    StatSummaryRow(total = students.size, passed = passCount, failed = failCount, average = avg)
                    
                    ExportButtons(
                        onExportExcel = { saveExcelLauncher.launch("GradeReport.xlsx") },
                        onExportPdf = { savePdfLauncher.launch("GradeReport.pdf") }
                    )
                    
                    ResultsTable(students, headers)
                }
            }
        }
    }
}
