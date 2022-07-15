package com.saraiva.felipe.mycamera.domain

import android.graphics.Bitmap
import android.graphics.Matrix
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString

object CameraImageProcessExtension {

    private const val MAX_SIZE = 2048

    fun resizeBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        var width: Int = bitmap.width
        var height: Int = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = MAX_SIZE
            height = (width / bitmapRatio).toInt()
        } else {
            height = MAX_SIZE
            width = (height * bitmapRatio).toInt()
        }
        val temp = Bitmap.createScaledBitmap(bitmap, width, height, true)
        val rotateMatrix = Matrix()
        rotateMatrix.postRotate(orientation.toFloat())
        val resultBitmap = Bitmap.createBitmap(temp, 0, 0, width, height, rotateMatrix, true)
        temp.recycle()
        return resultBitmap
    }

    fun generateImageFile(bitmap: Bitmap): Path {
        val file = kotlin.io.path.createTempFile(UUID.randomUUID().toString(), ".jpeg")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file.absolutePathString()))
        bitmap.recycle()
        return file
    }
}