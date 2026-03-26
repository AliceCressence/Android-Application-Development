package com.joelfah.gradecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.joelfah.gradecalculator.navigation.NavGraph
import com.joelfah.gradecalculator.viewmodel.GradeViewModel

/**
 * MainActivity — the single Activity that hosts the entire Compose UI.
 *
 * Wires together:
 * - [GradeViewModel] — shared state across all screens
 * - [NavGraph]       — navigation between Home, Input, Results, Export
 */
class MainActivity : ComponentActivity() {

    // ViewModel survives screen rotations via viewModels() delegate
    private val viewModel: GradeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    viewModel     = viewModel
                )
            }
        }
    }
}