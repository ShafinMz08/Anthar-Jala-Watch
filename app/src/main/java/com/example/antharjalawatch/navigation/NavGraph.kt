package com.example.antharjalawatch.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.antharjalawatch.ui.screens.*
import com.example.antharjalawatch.viewmodel.AppViewModel

object Routes {
    const val SPLASH   = "splash"
    const val HOME     = "home"
    const val LOG_DATA = "log_data"
    const val MAP      = "map"
    const val INSIGHTS = "insights"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"
    const val ABOUT    = "about"
}

@Composable
fun AppNavGraph(
    navController : NavHostController,
    viewModel     : AppViewModel
) {
    NavHost(
        navController    = navController,
        startDestination = Routes.SPLASH,
        enterTransition  = {
            fadeIn(animationSpec = tween(300)) +
                    slideInHorizontally(animationSpec = tween(300)) { it / 4 }
        },
        exitTransition   = {
            fadeOut(animationSpec = tween(200)) +
                    slideOutHorizontally(animationSpec = tween(200)) { -it / 4 }
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideInHorizontally(animationSpec = tween(300)) { -it / 4 }
        },
        popExitTransition  = {
            fadeOut(animationSpec = tween(200)) +
                    slideOutHorizontally(animationSpec = tween(200)) { it / 4 }
        }
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel  = viewModel,
                onLogData  = { navController.navigate(Routes.LOG_DATA) },
                onViewMap  = { navController.navigate(Routes.MAP) },
                onAnalytics = { navController.navigate(Routes.ANALYTICS) },
                onInsights = { navController.navigate(Routes.INSIGHTS) },
                onSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.LOG_DATA) {
            LogDataScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                viewModel  = viewModel,
                onBack     = { navController.popBackStack() },
                onInsights = { navController.navigate(Routes.INSIGHTS) }
            )
        }

        composable(Routes.INSIGHTS) {
            InsightsScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Routes.ANALYTICS) {
            AnalyticsScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
                onAbout   = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
