package com.joelfah.gradecalculator.navigation

/**
 * SEALED CLASS — all navigation routes in the app.
 *
 * Using a sealed class here (from the lecturer's notes) ensures the compiler
 * knows every possible screen — no typos in route strings.
 *
 * POLYMORPHISM: each screen object overrides `route` differently.
 */
sealed class Screen(val route: String) {
    object Home    : Screen("home")
    object Input   : Screen("input")
    object Results : Screen("results")
    object Export  : Screen("export")
}