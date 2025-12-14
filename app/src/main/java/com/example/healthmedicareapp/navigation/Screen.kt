package com.example.healthmedicareapp.navigation


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registration : Screen("registration")
    object ForgotPassword : Screen("forgot_password")
    object MedicalDetails : Screen("medical_details")
    object Dashboard : Screen("dashboard")
    object CalorieManage : Screen("calorie_manage")
    object SleepTrack : Screen("sleep_track")
    object HealthArticle : Screen("health_article")
    object MapsSearch : Screen("maps_search")
}