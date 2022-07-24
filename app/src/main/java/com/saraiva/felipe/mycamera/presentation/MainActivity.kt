package com.saraiva.felipe.mycamera.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.saraiva.felipe.mycamera.presentation.navigation.SetupNavController
import com.saraiva.felipe.mycamera.presentation.ui.theme.MyCameraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyCameraTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                SetupNavController(navController = navController)
            }
        }
    }
}