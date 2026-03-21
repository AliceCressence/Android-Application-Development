package com.joelfah.gradecalculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joelfah.gradecalculator.models.GradeSession
import com.joelfah.gradecalculator.ui.theme.AppRadius
import com.joelfah.gradecalculator.ui.theme.AppSpacing
import com.joelfah.gradecalculator.ui.theme.AppTypography
import com.joelfah.gradecalculator.ui.theme.Surface
import com.joelfah.gradecalculator.ui.theme.TextSecondary

@Composable
fun HistoryItem(session: GradeSession, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(AppRadius.card),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.xs)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = session.fileName, style = AppTypography.bodyLarge)
                Text(text = session.processedAt, style = AppTypography.labelSmall, color = TextSecondary)
            }
            StatsBadge(label = "Avg", value = String.format("%.1f", session.classAverage))
            Spacer(modifier = Modifier.width(AppSpacing.sm))
            StatsBadge(label = "Students", value = session.totalStudents.toString())
        }
    }
}

@Composable
fun StatsBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = AppTypography.bodyMedium)
        Text(text = label, style = AppTypography.labelSmall, color = TextSecondary)
    }
}
