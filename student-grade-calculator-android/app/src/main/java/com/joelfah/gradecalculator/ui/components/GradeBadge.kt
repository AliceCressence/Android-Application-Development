package com.joelfah.gradecalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joelfah.gradecalculator.ui.theme.BadgeBg
import com.joelfah.gradecalculator.ui.theme.BadgeFg
import com.joelfah.gradecalculator.ui.theme.Danger
import com.joelfah.gradecalculator.ui.theme.Success

@Composable
fun GradeBadge(grade: String, isPass: Boolean) {
    Box(
        modifier = Modifier
            .background(BadgeBg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .widthIn(min = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = grade,
            color = BadgeFg,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PassFailBadge(isPass: Boolean) {
    val color = if (isPass) Success else Danger
    Text(
        text = if (isPass) "PASS" else "FAIL",
        color = color,
        fontWeight = FontWeight.Bold
    )
}
