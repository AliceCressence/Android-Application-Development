package com.joelfah.gradecalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joelfah.gradecalculator.viewmodel.GradeViewModel
import factory.ExportFormat

/**
 * Export Screen — lets the user export results to Excel, HTML, or JSON.
 *
 * FACTORY METHOD in action:
 * Each button triggers viewModel.exportReport(format, context) which calls
 * ExporterFactory.create(format) internally — this screen never knows
 * which exporter class is used.
 *
 * POLYMORPHISM:
 * All three exporters implement ReportExporter — they're treated identically.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    viewModel: GradeViewModel,
    onNavigateBack: () -> Unit
) {
    val context      = LocalContext.current
    val exportStatus by viewModel.exportStatus.collectAsState()
    val isLoading    by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export Results", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("← Back", color = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Choose export format",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Files are saved to your device's external storage.\nOpen them with Excel, any browser, or a JSON viewer.",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Export format buttons ──────────────────────────────────────
            // FACTORY METHOD: each button passes a different ExportFormat
            // to the ViewModel which uses ExporterFactory.create() internally

            ExportFormatCard(
                emoji       = "📊",
                title       = "Excel (.xlsx)",
                description = "Colour-coded spreadsheet with grades",
                color       = Color(0xFF1B5E20),
                status      = exportStatus[ExportFormat.EXCEL.name],
                isLoading   = isLoading,
                onClick     = { viewModel.exportReport(ExportFormat.EXCEL, context) }
            )

            ExportFormatCard(
                emoji       = "🌐",
                title       = "HTML (.html)",
                description = "Styled web page — open in any browser",
                color       = Color(0xFF0D47A1),
                status      = exportStatus[ExportFormat.HTML.name],
                isLoading   = isLoading,
                onClick     = { viewModel.exportReport(ExportFormat.HTML, context) }
            )

            ExportFormatCard(
                emoji       = "{ }",
                title       = "JSON (.json)",
                description = "Structured data — ready for any API",
                color       = Color(0xFF4A148C),
                status      = exportStatus[ExportFormat.JSON.name],
                isLoading   = isLoading,
                onClick     = { viewModel.exportReport(ExportFormat.JSON, context) }
            )

            // ── Export All button ──────────────────────────────────────────
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    ExportFormat.values().forEach { format ->
                        viewModel.exportReport(format, context)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107)
                ),
                enabled = !isLoading
            ) {
                Text(
                    "Export All Formats",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1A237E))
                }
            }
        }
    }
}

@Composable
private fun ExportFormatCard(
    emoji: String,
    title: String,
    description: String,
    color: Color,
    status: String?,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 28.sp)
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(description, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Button(
                    onClick = onClick,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Export", color = Color.White)
                }
            }

            // Status message after export
            if (!status.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status,
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}