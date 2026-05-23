package com.example.healthmedicareapp.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthmedicareapp.presentation.auth.forgot_password.ForgotPasswordScreen
import com.example.healthmedicareapp.presentation.auth.login.LoginScreen
import com.example.healthmedicareapp.presentation.auth.registration.RegistrationScreen
import com.example.healthmedicareapp.presentation.calorie_manage.CalorieManageScreen
import com.example.healthmedicareapp.presentation.dashboard.DashboardScreen
import com.example.healthmedicareapp.presentation.health_article.HealthArticleScreen
import com.example.healthmedicareapp.presentation.maps.MapsSearchScreen
import com.example.healthmedicareapp.presentation.medical_details.MedicalDetailsScreen
import com.example.healthmedicareapp.presentation.sleep_track.SleepTrackScreen
import com.example.healthmedicareapp.ui.theme.LocalDarkMode
import androidx.compose.ui.unit.dp

private const val ANIM_DURATION = 350

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route,
        enterTransition  = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(ANIM_DURATION)
            ) + fadeIn(tween(ANIM_DURATION))
        },
        exitTransition   = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(ANIM_DURATION)
            ) + fadeOut(tween(ANIM_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(ANIM_DURATION)
            ) + fadeIn(tween(ANIM_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(ANIM_DURATION)
            ) + fadeOut(tween(ANIM_DURATION))
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister       = { navController.navigate(Screen.Registration.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToMedicalDetails = {
                    navController.navigate(Screen.MedicalDetails.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.MedicalDetails.route) {
            MedicalDetailsScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.MedicalDetails.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Screen.MainScreen.route,
            // Fade-through for main shell (no slide)
            enterTransition = { fadeIn(tween(ANIM_DURATION)) },
            exitTransition  = { fadeOut(tween(ANIM_DURATION)) },
        ) {
            MainScaffold(
                onNavigateToMaps = { navController.navigate(Screen.MapsSearch.route) }
            )
        }

        composable(Screen.MapsSearch.route) {
            MapsSearchScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main shell with bottom navigation bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MainScaffold(onNavigateToMaps: () -> Unit) {
    val tabNavController   = rememberNavController()
    val navBackStackEntry  by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val darkModeState      = LocalDarkMode.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                ) {
                BottomNavScreen.all.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick  = {
                            tabNavController.navigate(tab.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon  = { Icon(tab.icon, contentDescription = tab.label) },
                        label = {
                            Text(
                                tab.label,
                                fontSize   = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = MaterialTheme.colorScheme.primary,
                            selectedTextColor   = MaterialTheme.colorScheme.primary,
                            indicatorColor      = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = tabNavController,
            startDestination = Screen.Dashboard.route,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = { fadeIn(tween(280)) },
            exitTransition   = { fadeOut(tween(280)) },
            popEnterTransition = { fadeIn(tween(280)) },
            popExitTransition  = { fadeOut(tween(280)) }
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToCalorie  = {
                        tabNavController.navigate(Screen.CalorieManage.route) {
                            popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    onNavigateToSleep    = {
                        tabNavController.navigate(Screen.SleepTrack.route) {
                            popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    onNavigateToArticles = {
                        tabNavController.navigate(Screen.HealthArticle.route) {
                            popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                    onNavigateToMaps     = onNavigateToMaps,
                    isDarkMode           = darkModeState.isDark,
                    onToggleDarkMode     = darkModeState.toggle
                )
            }

            composable(Screen.CalorieManage.route) {
                CalorieManageScreen(onNavigateBack = null)
            }

            composable(Screen.SleepTrack.route) {
                SleepTrackScreen(onNavigateBack = null)
            }

            composable(Screen.HealthArticle.route) {
                HealthArticleScreen(onNavigateBack = null)
            }
        }
    }
}