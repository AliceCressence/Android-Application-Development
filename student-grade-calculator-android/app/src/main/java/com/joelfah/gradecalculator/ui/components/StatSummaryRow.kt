package com.joelfah.gradecalculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joelfah.gradecalculator.ui.theme.AppSpacing
import com.joelfah.gradecalculator.ui.theme.AppTypography
import com.joelfah.gradecalculator.ui.theme.Danger
import com.joelfah.gradecalculator.ui.theme.Success
import com.joelfah.gradecalculator.ui.theme.Surface
import com.joelfah.gradecalculator.ui.theme.TextSecondary

@Composable
fun StatSummaryRow(
    total: Int,
    passed: Int,
    failed: Int,
    average: Double
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.md)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.md).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(label = "Students", value = total.toString())
            StatItem(label = "Passed", value = passed.toString(), valueColor = Success)
            StatItem(label = "Failed", value = failed.toString(), valueColor = Danger)
            StatItem(label = "Avg", value = String.format("%.1f", average))
        }
    }
}

@Composable
fun StatItem(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(text = value, style = AppTypography.headlineMedium, color = valueColor, fontWeight = FontWeight.Bold)
        Text(text = label, style = AppTypography.labelSmall, color = TextSecondary)
    }
}
