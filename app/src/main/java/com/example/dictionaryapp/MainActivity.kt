package com.example.dictionaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionaryapp.presentation.MainScreen
import com.example.dictionaryapp.presentation.MainViewModel
import com.example.dictionaryapp.ui.theme.DictionaryAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DictionaryAppTheme {
                val vm = hiltViewModel<MainViewModel>()
                val state by vm.mainState.collectAsStateWithLifecycle()
                BarColor()
                MainScreen(state, vm::onEvent)
            }
        }
    }

    @Composable
    private fun BarColor() {
        val isDarkTheme = isSystemInDarkTheme()
        val color = MaterialTheme.colorScheme.background.toArgb()
        val colorDark = MaterialTheme.colorScheme.primary.toArgb()
        LaunchedEffect(isDarkTheme) {
            enableEdgeToEdge(
                statusBarStyle =
                if (isDarkTheme)
                    SystemBarStyle.dark(color)
                else
                    SystemBarStyle.light(color, colorDark)
            )
        }
    }
}