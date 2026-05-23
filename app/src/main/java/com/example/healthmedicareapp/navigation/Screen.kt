package com.example.healthmedicareapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login          : Screen("login")
    object Registration   : Screen("registration")
    object ForgotPassword : Screen("forgot_password")
    object MedicalDetails : Screen("medical_details")
    object MainScreen     : Screen("main_screen")   // wrapper for bottom-nav tabs
    object Dashboard      : Screen("dashboard")
    object CalorieManage  : Screen("calorie_manage")
    object SleepTrack     : Screen("sleep_track")
    object HealthArticle  : Screen("health_article")
    object MapsSearch     : Screen("maps_search")
}

/** Items rendered in the BottomNavigationBar. */
sealed class BottomNavScreen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home     : BottomNavScreen(Screen.Dashboard.route,     "Home",     Icons.Rounded.Home)
    object Calories : BottomNavScreen(Screen.CalorieManage.route, "Calories", Icons.Rounded.LocalDining)
    object Sleep    : BottomNavScreen(Screen.SleepTrack.route,    "Sleep",    Icons.Rounded.Bedtime)
    object Articles : BottomNavScreen(Screen.HealthArticle.route, "Articles", Icons.Rounded.Article)

    companion object {
        val all = listOf(Home, Calories, Sleep, Articles)
    }
}