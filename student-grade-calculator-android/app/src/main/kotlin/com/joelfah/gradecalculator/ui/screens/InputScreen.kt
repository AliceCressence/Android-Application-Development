package com.joelfah.gradecalculator.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.joelfah.gradecalculator.viewmodel.GradeViewModel
import java.io.File

/**
 * Upload Excel Screen (replaces manual student entry)
 *
 * Uses your existing: viewModel.addAndCalculateStudents(...)
 * You still need to implement Excel parsing in the ViewModel.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    viewModel: GradeViewModel,
    onNavigateToResults: () -> Unit
) {
    // ── Local UI state ────────────────────────────────────────────────────────
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isCalculated by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // File picker launcher (Excel only)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            // Get filename
            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    selectedFileName = if (nameIndex != -1) c.getString(nameIndex) else "students.xlsx"
                }
            } ?: run { selectedFileName = "students.xlsx" }

            isCalculated = false
            errorMessage = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Excel & Calculate Grades", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Upload Card ─────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "📤 Upload Student Data (Excel)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "The Excel file should contain student information.\n" +
                                    "After upload, grades will be calculated using your existing logic.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A237E)
                            )
                        ) {
                            Text("Select Excel File (.xlsx)")
                        }

                        if (selectedFileName.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "✅ Selected: $selectedFileName",
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1A237E)
                            )
                        }
                    }
                }
            }

            // ── Calculate Button (only show if file selected) ───────────────
            if (selectedUri != null && !isCalculated) {
                item {
                    Button(
                        onClick = {
                            selectedUri?.let { uri ->
                                isProcessing = true
                                errorMessage = ""

                                try {
                                    viewModel.importAndCalculateFromExcel(uri, context)
                                    isCalculated = true
                                } catch (e: Exception) {
                                    errorMessage = "Failed to read Excel file: ${e.message}"
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E)
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing Excel File...")
                        } else {
                            Text("Calculate Grades from Excel →", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── Error Message ───────────────────────────────────────────────
            if (errorMessage.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // ── Success & Export Section ────────────────────────────────────
            if (isCalculated) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "✅ Grades calculated successfully from Excel!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Export Results",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Export Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.exportToExcel(context)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Excel")
                        }

                        Button(
                            onClick = { viewModel.exportToHtml(context) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text("HTML")
                        }

                        Button(
                            onClick = { viewModel.exportToJson(context) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C27B0)
                            )
                        ) {
                            Text("JSON")
                        }
                    }
                }

                // Share Button
                item {
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Grade Calculation Results")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Student grades have been successfully calculated from the uploaded Excel file.\n\n" +
                                            "You can view the detailed results in the app or check the exported files."
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Results"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E)
                        )
                    ) {
                        Text("📤 Share Results", fontWeight = FontWeight.Bold)
                    }
                }

                // Navigate to Results
                item {
                    OutlinedButton(
                        onClick = onNavigateToResults,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Detailed Results in App →")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}