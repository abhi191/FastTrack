package com.fasttrack.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fasttrack.app.theme.ThemeMode
import com.fasttrack.app.ui.settings.SettingsScreen
import com.fasttrack.app.ui.stages.StagesScreen
import com.fasttrack.app.ui.timer.TimerScreen
import com.fasttrack.app.ui.timer.TimerViewModel
import com.fasttrack.app.data.history.HistoryDao
import com.fasttrack.app.ui.history.HistoryScreen

sealed class Screen(val route: String) {
    data object Timer : Screen("timer")
    data object Stages : Screen("stages")
    data object History : Screen("history")
    data object Settings : Screen("settings")
}

@Composable
fun FastTrackNavHost(
    navController: NavHostController,
    timerViewModel: TimerViewModel,
    historyDao: HistoryDao,
    currentThemeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = Screen.Timer.route, modifier = modifier) {
        composable(Screen.Timer.route) { TimerScreen(viewModel = timerViewModel) }
        composable(Screen.Stages.route) { StagesScreen(viewModel = timerViewModel) }
        composable(Screen.History.route) { HistoryScreen(historyDao = historyDao) }
        composable(Screen.Settings.route) { SettingsScreen(currentThemeMode = currentThemeMode, onThemeChange = onThemeChange) }
    }
}
