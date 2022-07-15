package com.saraiva.felipe.mycamera.presentation.navigation

import java.nio.file.Path
import kotlin.io.path.absolutePathString

sealed class Screen(val route: String) {
    object SplashScreen: Screen("splash_screen" )
    object PreviewScreen: Screen("preview_screen" )
    object PictureScreen: Screen("photo_screen/{imagePath}") {
        fun withFile(path: Path): String {
            return "photo_screen/" + path.absolutePathString()
        }
    }
    object VideoScreen: Screen("video_screen")
}