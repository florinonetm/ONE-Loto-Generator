package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GeneratorScreen
import com.example.ui.theme.OneLotoTheme
import com.example.viewmodel.LotoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OneLotoTheme {
                val viewModel: LotoViewModel = viewModel(
                    factory = LotoViewModel.provideFactory(application)
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                GeneratorScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}
