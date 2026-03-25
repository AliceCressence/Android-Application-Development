package com.joelfah.gradecalculator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joelfah.gradecalculator.ui.screens.ExportScreen
import com.joelfah.gradecalculator.ui.screens.HomeScreen
import com.joelfah.gradecalculator.ui.screens.InputScreen
import com.joelfah.gradecalculator.ui.screens.ResultsScreen
import com.joelfah.gradecalculator.viewmodel.GradeViewModel

/**
 * Sets up the navigation graph connecting all 4 screens.
 *
 * The [GradeViewModel] is passed down to every screen so they all share
 * the same state — one single source of truth.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: GradeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToInput = { navController.navigate(Screen.Input.route) }
            )
        }

        composable(Screen.Input.route) {
            InputScreen(
                viewModel    = viewModel,
                onNavigateToResults = {
                    navController.navigate(Screen.Results.route)
                }
            )
        }

        composable(Screen.Results.route) {
            ResultsScreen(
                viewModel = viewModel,
                onNavigateToExport = {
                    navController.navigate(Screen.Export.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Export.route) {
            ExportScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}