package com.example.healthmedicareapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healthmedicareapp.presentation.auth.login.LoginScreen
import com.example.healthmedicareapp.presentation.auth.registration.RegistrationScreen
import com.example.healthmedicareapp.presentation.auth.forgot_password.ForgotPasswordScreen
import com.example.healthmedicareapp.presentation.medical_details.MedicalDetailsScreen
import com.example.healthmedicareapp.presentation.dashboard.DashboardScreen
import com.example.healthmedicareapp.presentation.calorie_manage.CalorieManageScreen
import com.example.healthmedicareapp.presentation.sleep_track.SleepTrackScreen
import com.example.healthmedicareapp.presentation.health_article.HealthArticleScreen
import com.example.healthmedicareapp.presentation.maps.MapsSearchScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Registration.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToMedicalDetails = {
                    navController.navigate(Screen.MedicalDetails.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MedicalDetails.route) {
            MedicalDetailsScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.MedicalDetails.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCalorie = {
                    navController.navigate(Screen.CalorieManage.route)
                },
                onNavigateToSleep = {
                    navController.navigate(Screen.SleepTrack.route)
                },
                onNavigateToArticles = {
                    navController.navigate(Screen.HealthArticle.route)
                },
                onNavigateToMaps = {
                    navController.navigate(Screen.MapsSearch.route)
                }
            )
        }

        composable(Screen.CalorieManage.route) {
            CalorieManageScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SleepTrack.route) {
            SleepTrackScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.HealthArticle.route) {
            HealthArticleScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MapsSearch.route) {
            MapsSearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}