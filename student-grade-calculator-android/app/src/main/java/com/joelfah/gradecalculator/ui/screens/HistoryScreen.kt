package com.joelfah.gradecalculator.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joelfah.gradecalculator.data.HistoryRepository
import com.joelfah.gradecalculator.models.GradeSession
import com.joelfah.gradecalculator.ui.components.EmptyState
import com.joelfah.gradecalculator.ui.components.HistoryItem
import com.joelfah.gradecalculator.ui.components.ResultsTable
import com.joelfah.gradecalculator.ui.components.StatSummaryRow
import com.joelfah.gradecalculator.ui.theme.AppSpacing
import com.joelfah.gradecalculator.ui.theme.AppTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    repository: HistoryRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var sessions by remember { mutableStateOf<List<GradeSession>>(emptyList()) }
    var selectedSession by remember { mutableStateOf<GradeSession?>(null) }
    
    // Bottom Sheet state
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            sessions = repository.getAllSessions()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History", style = AppTypography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (sessions.isNotEmpty()) {
                        TextButton(onClick = {
                            scope.launch(Dispatchers.IO) {
                                repository.clearAll()
                                sessions = emptyList() // Clear local state immediately
                                // Re-fetch to be sure? No need if cleared.
                            }
                        }) {
                            Text("Clear All")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(AppSpacing.md)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (sessions.isEmpty()) {
                EmptyState("No history yet.")
            } else {
                LazyColumn {
                    items(sessions) { session ->
                        HistoryItem(session = session, onClick = {
                            selectedSession = session
                            showBottomSheet = true
                        })
                    }
                }
            }
        }
    }

    if (showBottomSheet && selectedSession != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            val session = selectedSession!!
            Column(modifier = Modifier.padding(AppSpacing.md)) {
                Text(text = session.fileName, style = AppTypography.headlineMedium)
                Text(text = session.processedAt, style = AppTypography.labelSmall)
                
                StatSummaryRow(
                    total = session.totalStudents,
                    passed = session.passCount,
                    failed = session.failCount,
                    average = session.classAverage
                )
                
                // Use a Box with max height for the table list
                Box(modifier = Modifier.heightIn(max = 500.dp)) {
                    ResultsTable(students = session.students, headers = session.subjectHeaders)
                }
                
                Box(modifier = Modifier.height(32.dp))
            }
        }
    }
}
