package com.joelfah.gradecalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.joelfah.gradecalculator.models.Student
import com.joelfah.gradecalculator.ui.theme.AppSpacing
import com.joelfah.gradecalculator.ui.theme.AppTypography
import com.joelfah.gradecalculator.ui.theme.Surface
import com.joelfah.gradecalculator.ui.theme.SurfaceDim
import com.joelfah.gradecalculator.ui.theme.TextPrimary

@Composable
fun ResultsTable(students: List<Student>, headers: List<String>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            TableHeader()
        }
        itemsIndexed(students) { index, student ->
            TableRow(student = student, isEven = index % 2 == 0)
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(vertical = AppSpacing.sm, horizontal = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Name", modifier = Modifier.weight(2f), style = AppTypography.bodyMedium, fontWeight = FontWeight.Bold)
        Text("Avg", modifier = Modifier.weight(1f), style = AppTypography.bodyMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("Grade", modifier = Modifier.weight(1f), style = AppTypography.bodyMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("Status", modifier = Modifier.weight(1.5f), style = AppTypography.bodyMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun TableRow(student: Student, isEven: Boolean) {
    val bgColor = if (isEven) Surface else SurfaceDim
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(vertical = AppSpacing.sm, horizontal = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name
        Text(text = student.name, modifier = Modifier.weight(2f), style = AppTypography.bodyLarge, color = TextPrimary)
        
        // Avg
        Text(text = String.format("%.1f", student.average), modifier = Modifier.weight(1f), style = AppTypography.bodyLarge, textAlign = TextAlign.Center)
        
        // Grade
        val grade = student.letterGrade ?: "-"
        val isPass = student.isPass
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            GradeBadge(grade = grade, isPass = isPass)
        }
        
        // Status
        Box(modifier = Modifier.weight(1.5f), contentAlignment = Alignment.Center) {
            PassFailBadge(isPass = isPass)
        }
    }
}
