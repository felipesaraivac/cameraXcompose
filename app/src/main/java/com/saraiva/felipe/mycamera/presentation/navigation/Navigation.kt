package com.saraiva.felipe.mycamera.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.saraiva.felipe.mycamera.presentation.CameraVideoModel
import com.saraiva.felipe.mycamera.presentation.screens.camera.CameraScreen
import com.saraiva.felipe.mycamera.presentation.screens.preview.ViewerScreen


@Composable
fun SetupNavController(navController: NavHostController) {
    val viewModel: CameraVideoModel = viewModel()
    NavHost(navController = navController, startDestination = Screen.CameraScreen.route) {
        composable(route = Screen.SplashScreen.route) {

        }
        composable(route = Screen.CameraScreen.route) {
            CameraScreen(navController, viewModel)
        }
        composable(
            route = Screen.PictureScreen.route
        ) {
            if (viewModel.imageFilePicture != null) {
                ViewerScreen(navController = navController, image = viewModel.imageFilePicture!!)
            } else {
                navController.popBackStack()
            }
        }
        composable(route = Screen.VideoScreen.route) {

        }
    }
}

