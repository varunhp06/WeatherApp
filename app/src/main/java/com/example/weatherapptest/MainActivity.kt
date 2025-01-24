package com.example.weatherapptest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapptest.ui.theme.MainView
import com.example.weatherapptest.ui.theme.WeatherAppTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LocationViewModel = viewModel()
            WeatherAppTestTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    MainView(viewModel)
                }
            }
        }
    }
}