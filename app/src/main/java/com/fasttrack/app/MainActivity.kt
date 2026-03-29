package com.fasttrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import com.fasttrack.app.data.ThemePreferences
import com.fasttrack.app.theme.FastTrackTheme
import com.fasttrack.app.theme.ThemeMode
import com.fasttrack.app.ui.timer.TimerViewModel
import com.fasttrack.app.ui.timer.TimerViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val timerViewModel: TimerViewModel by viewModels {
        val application = application as FastTrackApplication
        TimerViewModelFactory(application.database.historyDao(), WorkManager.getInstance(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by ThemePreferences.getThemeMode(this)
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val scope = rememberCoroutineScope()

            FastTrackTheme(themeMode = themeMode) {
                FastTrackApp(
                    timerViewModel = timerViewModel,
                    currentThemeMode = themeMode,
                    historyDao = (application as FastTrackApplication).database.historyDao(),
                    onThemeChange = { mode ->
                        scope.launch { ThemePreferences.setThemeMode(this@MainActivity, mode) }
                    },
                )
            }
        }
    }
}
