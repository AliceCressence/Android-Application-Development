package com.joelfah.gradecalculator.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joelfah.gradecalculator.ui.theme.AppRadius
import com.joelfah.gradecalculator.ui.theme.AppSpacing
import com.joelfah.gradecalculator.ui.theme.AppTypography
import com.joelfah.gradecalculator.ui.theme.BorderDash
import com.joelfah.gradecalculator.ui.theme.TextSecondary

@Composable
fun FilePickerCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderDash, shape = RoundedCornerShape(AppRadius.card))
            .clickable { onClick() }
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Tap to select Excel File", style = AppTypography.headlineMedium)
        Text("Supports .xlsx", style = AppTypography.labelSmall, color = TextSecondary)
    }
}
