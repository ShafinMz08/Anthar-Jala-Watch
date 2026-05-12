package com.example.antharjalawatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.antharjalawatch.navigation.AppNavGraph
import com.example.antharjalawatch.ui.theme.AntharJalaTheme
import com.example.antharjalawatch.viewmodel.AppViewModel

/**
 * MainActivity — single Activity for the entire app.
 *
 * Architecture:
 *   MainActivity
 *     └── AntharJalaTheme (dark/light from ViewModel)
 *           └── Surface
 *                 └── AppNavGraph (Splash → Home → LogData | Map → Insights | Analytics | Settings → About)
 *
 * AppViewModel is scoped to this Activity via viewModels() delegate.
 * All screens share the same ViewModel instance.
 */
class MainActivity : ComponentActivity() {

    // Activity-scoped ViewModel — shared across all screens
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Dark mode state comes from ViewModel so it persists across navigation
            AntharJalaTheme(darkTheme = appViewModel.darkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        viewModel     = appViewModel
                    )
                }
            }
        }
    }
}
