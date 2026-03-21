package com.joelfah.gradecalculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joelfah.gradecalculator.ui.theme.Accent
import com.joelfah.gradecalculator.ui.theme.AccentFg
import com.joelfah.gradecalculator.ui.theme.AppSpacing

@Composable
fun ExportButtons(
    onExportExcel: () -> Unit,
    onExportPdf: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = AppSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Button(
            onClick = onExportExcel,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = AccentFg)
        ) {
            Text("Export Excel")
        }
        
        OutlinedButton(
            onClick = onExportPdf,
            modifier = Modifier.weight(1f)
        ) {
            Text("Export PDF", color = Accent)
        }
    }
}
