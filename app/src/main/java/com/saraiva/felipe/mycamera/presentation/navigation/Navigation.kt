package com.saraiva.felipe.mycamera.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.saraiva.felipe.mycamera.presentation.screens.preview.CameraScreen
import com.saraiva.felipe.mycamera.presentation.util.Constants.IMAGE_PATH


@Composable
fun SetupNavController(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.PreviewScreen.route) {
        composable(route = Screen.SplashScreen.route) {

        }
        composable(route = Screen.PreviewScreen.route) {
            CameraScreen(navController)
        }
        composable(
            route = Screen.PictureScreen.route,
            arguments = listOf(navArgument(IMAGE_PATH) {
                type = NavType.StringType
            })
        ) {

        }
        composable(route = Screen.VideoScreen.route) {

        }
    }
}