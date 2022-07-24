package com.saraiva.felipe.mycamera.presentation.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash_screen")
    object CameraScreen : Screen("camera_screen")
    object PictureScreen : Screen("photo_screen")
    object VideoScreen : Screen("video_screen")
}

@Parcelize
data class FileInfo(val name: String, val folder: String): Parcelable {

    val absolutePath get() = "$folder/$name"

    companion object {
        fun fromPath(path: Path): FileInfo {
            val file = File(path.absolutePathString())
            return FileInfo(file.name, file.parentFile?.absolutePath ?: "")
        }
    }
}

fun Path.toFileInfo(): FileInfo {
    return FileInfo.fromPath(this)
}
