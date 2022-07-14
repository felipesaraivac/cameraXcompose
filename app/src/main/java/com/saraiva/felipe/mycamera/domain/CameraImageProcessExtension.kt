package com.saraiva.felipe.mycamera.domain

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.media.ImageReader
import android.util.Size
import com.saraiva.felipe.mycamera.presentation.CameraFragment.Companion.MAX_SIZE
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString

object CameraImageProcessExtension {

    fun CameraController.getImageReader(cameraCharacteristics: CameraCharacteristics, defaultSize: Size): ImageReader {
        val jpegSizes: Array<Size>? =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                .getOutputSizes(ImageFormat.JPEG)
        var width = defaultSize.width
        var height = defaultSize.height
        if (jpegSizes != null && jpegSizes.isNotEmpty()) {
            width = jpegSizes[0].width
            height = jpegSizes[0].height
        }
        return ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
    }

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