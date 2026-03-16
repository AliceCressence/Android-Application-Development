package com.gradecalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gradecalc.model.GradeCalculator
import com.gradecalc.model.Student

@Composable
fun GradeCalculatorScreen() {
    val calc = remember { GradeCalculator() }
    var students by remember { mutableStateOf(calc.getAllStudents()) }
    var nameInput by remember { mutableStateOf("") }
    var scoreInput by remember { mutableStateOf("") }
    var maxScoreInput by remember { mutableStateOf("100") }
    var errorMessage by remember { mutableStateOf("") }

    // Refresh list helper — lambda
    val refreshList = { students = calc.getAllStudents() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .padding(16.dp)
    ) {
        // ── TITLE ────────────────────────────────────────
        Text(
            text = "🎓 Student Grade Calculator",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Divider(color = Color(0xFF2C3E50), thickness = 2.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // ── INPUT CARD ───────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Add Subject",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2C3E50)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Subject Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = scoreInput,
                        onValueChange = { scoreInput = it },
                        label = { Text("Score") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = maxScoreInput,
                        onValueChange = { maxScoreInput = it },
                        label = { Text("Max Score") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        // Validate inputs using null safety
                        val name = nameInput.trim()
                        val score = scoreInput.toDoubleOrNull()
                        val max = maxScoreInput.toDoubleOrNull()

                        when {
                            name.isEmpty() ->
                                errorMessage = "Please enter a subject name"
                            score == null ->
                                errorMessage = "Please enter a valid score"
                            max == null || max <= 0 ->
                                errorMessage = "Please enter a valid max score"
                            score < 0 || score > max ->
                                errorMessage = "Score must be between 0 and max score"
                            else -> {
                                calc.addStudent(
                                    Student(name, score, max)
                                )
                                // Lambda clears inputs
                                listOf(
                                    { nameInput = "" },
                                    { scoreInput = "" },
                                    { maxScoreInput = "100" },
                                    { errorMessage = "" }
                                ).forEach { it() }
                                refreshList()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF27AE60)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("➕ Add Subject", color = Color.White,
                        fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── SUBJECT LIST ─────────────────────────────────
        if (students.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No subjects added yet.\nAdd a subject above to get started!",
                    color = Color(0xFF95A5A6),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(students) { index, student ->
                    SubjectRow(
                        student = student,
                        calc = calc,
                        onDelete = {
                            calc.deleteStudent(index)
                            refreshList()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── SUMMARY BAR ──────────────────────────────────
        if (students.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2C3E50)
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryItem(
                        label = "Average",
                        value = "${"%.1f".format(calc.calculate())}%",
                        color = Color.White
                    )
                    SummaryItem(
                        label = "GPA",
                        value = "%.2f".format(calc.calculateGPA()),
                        color = Color(0xFFF1C40F)
                    )
                    SummaryItem(
                        label = "Status",
                        value = calc.getOverallPassFail(),
                        color = Color(0xFF2ECC71)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Delete All Button
            Button(
                onClick = {
                    calc.deleteAll()
                    refreshList()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE74C3C)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🗑 Delete All", color = Color.White,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── SUBJECT ROW ──────────────────────────────────────────
@Composable
fun SubjectRow(
    student: Student,
    calc: GradeCalculator,
    onDelete: () -> Unit
) {
    val percentage = student.percentage
    val grade = percentage?.let { calc.getGradeLetter(it) } ?: "--"
    val status = percentage?.let { calc.getPassFail(it) } ?: "No score"

    // Color based on grade
    val gradeColor = when (grade) {
        "A+", "A" -> Color(0xFF27AE60)
        "B"       -> Color(0xFF2980B9)
        "C"       -> Color(0xFFF39C12)
        "D"       -> Color(0xFFE67E22)
        else      -> Color(0xFFE74C3C)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Grade badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(gradeColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = grade,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Subject info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = if (percentage != null)
                        "${"%.1f".format(student.score)} / " +
                                "${"%.1f".format(student.maxScore)} " +
                                "(${"%.1f".format(percentage)}%)"
                    else "No score provided",
                    fontSize = 13.sp,
                    color = Color(0xFF7F8C8D)
                )
                Text(
                    text = status,
                    fontSize = 12.sp,
                    color = if (status.contains("PASS"))
                        Color(0xFF27AE60) else Color(0xFFE74C3C)
                )
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFE74C3C)
                )
            }
        }
    }
}

// ── SUMMARY ITEM ─────────────────────────────────────────
@Composable
fun SummaryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color(0xFF95A5A6),
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}