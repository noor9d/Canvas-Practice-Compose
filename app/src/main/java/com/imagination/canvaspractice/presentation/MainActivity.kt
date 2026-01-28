package com.imagination.canvaspractice.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.imagination.canvaspractice.presentation.navigation.AppNavigation
import com.imagination.canvaspractice.ui.theme.CanvasPracticeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasPracticeTheme {
                val viewModel: MainViewModel by viewModels()
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}