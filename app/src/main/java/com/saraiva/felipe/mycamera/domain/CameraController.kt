@file:Suppress("DEPRECATION")

package com.saraiva.felipe.mycamera.domain

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresPermission
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.saraiva.felipe.mycamera.domain.CameraImageProcessExtension.getImageReader
import com.saraiva.felipe.mycamera.domain.CameraImageProcessExtension.resizeBitmap
import com.saraiva.felipe.mycamera.presentation.CameraFragment.Companion.MAX_PREVIEW_HEIGHT
import com.saraiva.felipe.mycamera.presentation.CameraFragment.Companion.MAX_PREVIEW_WIDTH
import com.saraiva.felipe.mycamera.presentation.CameraFragment.Companion.ORIENTATIONS
import com.saraiva.felipe.mycamera.presentation.CameraFragment.Companion.VIDEO_ENCODING_BITRATE
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*
import kotlin.io.path.absolutePathString

enum class FlashMode {
    FLASH_MODE_OFF, FLASH_MODE_ON, FLASH_MODE_AUTO
}

class CameraController(
    private val context: Context,
    private val previewView: PreviewView,
    private val previewSize: Size
) {

    private var currentCameraInfo: CameraInfo? = null
    private var mainExecutor = ContextCompat.getMainExecutor(context)

    /**
     * State Variables
     */
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    private val hasFrontCamera = cameraProviderFuture.get()
        .hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null

    fun openCamera(lifecycleOwner: LifecycleOwner) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder().build()
//            videoCapture = VideoCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (_: Exception) {}
        }, mainExecutor)
    }

    private var onImageAvailable: ((Bitmap) -> Unit)? = null
    fun takePicture(action: (Bitmap) -> Unit) {
        val imageCapture = imageCapture ?: return

        onImageAvailable = action
        imageCapture.takePicture(mainExecutor, onImageCapture)
    }

    fun switchCamera() {
        when(currentCameraInfo?.cameraSelector) {
            CameraSelector.DEFAULT_FRONT_CAMERA -> {

            }
            CameraSelector.DEFAULT_FRONT_CAMERA -> {

            }
        }

    }

    private val onImageCapture = object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val buffer: ByteBuffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)
            val exifInterface =
                androidx.exifinterface.media.ExifInterface(ByteArrayInputStream(bytes))
            var rotation = exifInterface.rotationDegrees
//            if (currentCameraInfo?.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
//                rotation += 180 //degrees

            val bitmap = resizeBitmap(
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size),
                rotation
            )
            mainExecutor.execute {
                onImageAvailable?.invoke(bitmap)
            }
        }

        override fun onError(exception: ImageCaptureException) {
            Log.d("Hello", "PROBLEM")
        }
    }
}