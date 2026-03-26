package com.joelfah.gradecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joelfah.gradecalculator.models.Routes
import com.joelfah.gradecalculator.ui.screens.HistoryScreen
import com.joelfah.gradecalculator.ui.screens.HomeScreen
import com.joelfah.gradecalculator.ui.theme.GradeApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as GradeApplication
        val repository = app.historyRepository

        setContent {
            GradeApplicationTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = Routes.HOME) {
                        composable(Routes.HOME) {
                            HomeScreen(
                                repository = repository,
                                onNavigateToHistory = { navController.navigate(Routes.HISTORY) }
                            )
                        }
                        composable(Routes.HISTORY) {
                            HistoryScreen(
                                repository = repository,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
