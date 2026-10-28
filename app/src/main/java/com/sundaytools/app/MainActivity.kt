package com.sundaytools.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sundaytools.app.overlay.OverlayBubbleService
import com.sundaytools.app.ui.screen.CloudRecordsScreen
import com.sundaytools.app.ui.screen.HistoryDetailScreen
import com.sundaytools.app.ui.screen.HistoryScreen
import com.sundaytools.app.ui.screen.HomeScreen
import com.sundaytools.app.ui.screen.SettingsScreen
import com.sundaytools.app.ui.theme.SundayToolsTheme
import com.sundaytools.app.viewmodel.SettingsViewModel
import com.sundaytools.app.worker.ArchiveScheduler
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single Activity hosting the whole in-app UI (Home, History, Settings).
 * The floating bubble itself lives entirely in [OverlayBubbleService] and is
 * independent of this Activity's lifecycle.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.settings.collectAsState()

            androidx.compose.runtime.LaunchedEffect(settings.archiveHour, settings.archiveMinute) {
                ArchiveScheduler.schedule(this@MainActivity, settings.archiveHour, settings.archiveMinute)
            }

            SundayToolsTheme(darkTheme = settings.darkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onOpenHistory = { navController.navigate("history") },
                                onOpenSettings = { navController.navigate("settings") },
                                onOpenCloudRecords = { navController.navigate("cloud") },
                                onRequestOverlay = { requestOverlayPermissionThenStart() }
                            )
                        }
                        composable("cloud") {
                            CloudRecordsScreen(onBack = { navController.popBackStack() })
                        }
                        composable("history") {
                            HistoryScreen(
                                onBack = { navController.popBackStack() },
                                onOpenDay = { dayKey -> navController.navigate("history/$dayKey") }
                            )
                        }
                        composable("history/{dayKey}") { backStackEntry ->
                            val dayKey = backStackEntry.arguments?.getString("dayKey").orEmpty()
                            HistoryDetailScreen(dayKey = dayKey, onBack = { navController.popBackStack() })
                        }
                        composable("settings") {
                            SettingsScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }

    /**
     * The bubble requires SYSTEM_ALERT_WINDOW. On Android 6+ this must be granted
     * via the special "draw over other apps" settings screen rather than a normal
     * runtime permission dialog.
     */
    private fun requestOverlayPermissionThenStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        } else {
            OverlayBubbleService.start(this)
        }
    }

    override fun onResume() {
        super.onResume()
        // If the user just granted the overlay permission and comes back, start the bubble.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            // Left to an explicit "Start Bubble" tap on Home to avoid surprising the user;
            // HomeScreen exposes a button that calls requestOverlayPermissionThenStart() again.
        }
    }
}
