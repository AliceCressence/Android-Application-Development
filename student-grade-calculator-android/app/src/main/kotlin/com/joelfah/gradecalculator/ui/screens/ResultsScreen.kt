package com.joelfah.gradecalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joelfah.gradecalculator.viewmodel.GradeViewModel
import models.Student

/**
 * Results Screen — displays computed grades for every student.
 *
 * POLYMORPHISM in action:
 * - student.describe()     → calls Undergraduate or Graduate version
 * - student.studentType()  → returns "Undergraduate" or "Graduate"
 * - student.grade.isPassing → sealed Grade class property
 * - student.summaryLine()  → shared method from abstract Student
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: GradeViewModel,
    onNavigateToExport: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results", fontWeight = FontWeight.Bold) },
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

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF1A237E))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Summary cards ──────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        label = "Total",
                        value = viewModel.totalCount().toString(),
                        color = Color(0xFF1A237E),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Passed",
                        value = viewModel.passCount().toString(),
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Failed",
                        value = viewModel.failCount().toString(),
                        color = Color(0xFFC62828),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        label = "UG",
                        value = viewModel.ugCount().toString(),
                        color = Color(0xFF1565C0),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Graduate",
                        value = viewModel.grCount().toString(),
                        color = Color(0xFF6A1B9A),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    "📋 Student Results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            // ── Student result cards ───────────────────────────────────────
            // POLYMORPHISM: student.describe() and student.studentType()
            // dispatch to the correct subclass version at runtime
            items(students) { student ->
                StudentResultCard(student = student)
            }

            // ── Export button ──────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToExport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC107)
                    )
                ) {
                    Text(
                        "Export Results →",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun StudentResultCard(student: Student) {
    val isPass      = student.grade.isPassing
    val bgColor     = if (isPass) Color(0xFFF1F8E9) else Color(0xFFFFEBEE)
    val statusColor = if (isPass) Color(0xFF2E7D32) else Color(0xFFC62828)
    val typeBadgeColor = if (student.studentType() == "Undergraduate")
        Color(0xFF1565C0) else Color(0xFF6A1B9A)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header: ID + type badge + status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    student.studentId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Student type badge — POLYMORPHISM
                    Box(
                        modifier = Modifier
                            .background(typeBadgeColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            // studentType() dispatches to right subclass
                            student.studentType().take(2).uppercase(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Status badge — sealed Grade.isPassing
                    Box(
                        modifier = Modifier
                            .background(statusColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            student.grade.status(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Name — describe() is polymorphic
            Text(student.name, fontSize = 15.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))

            // Grade details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GradeDetail("Average", "${"%.2f".format(student.average)}")
                GradeDetail("Grade",   student.grade.letter)
                GradeDetail("GPA",     "${student.grade.gpa}")
            }
        }
    }
}

@Composable
private fun GradeDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A237E))
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}