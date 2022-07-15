package com.saraiva.felipe.mycamera.presentation.screens.preview

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.saraiva.felipe.mycamera.domain.CameraImageProcessExtension
import com.saraiva.felipe.mycamera.presentation.navigation.Screen
import com.saraiva.felipe.mycamera.presentation.screens.preview.CameraController.CameraPreview
import com.saraiva.felipe.mycamera.presentation.screens.preview.CameraController.takePicture
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Composable
fun CameraScreen(navController: NavHostController) {
    ConstraintLayout {
        val (preview, cameraBtn) = createRefs()
        val context = LocalContext.current
        CameraPreview(Modifier.constrainAs(preview) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        Button(
            onClick = {
                takePicture(context = context) {
                    navController.navigate(Screen.PictureScreen.withFile(it))
                }
            },
            Modifier.constrainAs(cameraBtn) {
                bottom.linkTo(parent.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text(text = "Hello")
        }
    }
}

private object CameraController: ImageCapture.OnImageCapturedCallback() {

    var imageCapture: ImageCapture? = null
    var videoCapture: VideoCapture<Recorder>? = null
    var onImageReady: (Path) -> Unit = {}

    @Composable
    fun CameraPreview(
        modifier: Modifier = Modifier,
        scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
        cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    ) {
        val coroutineScope = rememberCoroutineScope()
        val lifecycleOwner = LocalLifecycleOwner.current
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val previewView = PreviewView(context).apply {
                    this.scaleType = scaleType
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }

                // CameraX Preview UseCase
                val previewUseCase = androidx.camera.core.Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                imageCapture = ImageCapture.Builder().build()

                val recorder = Recorder.Builder().setQualitySelector(
                    QualitySelector.fromOrderedList(
                        listOf(Quality.FHD, Quality.HD, Quality.SD),
                        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
                    )
                ).build()
                videoCapture = VideoCapture.withOutput(recorder)

                coroutineScope.launch {
                    val cameraProvider = context.getCameraProvider()
                    try {
                        // Must unbind the use-cases before rebinding them.
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            previewUseCase,
                            imageCapture,
                            videoCapture
                        )
                    } catch (ex: Exception) {
                        imageCapture = null
                        videoCapture = null
                        onImageReady = {}
                        Log.e("CameraPreview", "Use case binding failed", ex)
                    }
                }
                previewView
            }
        )
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { future ->
                future.addListener({
                    continuation.resume(future.get())
                }, executor)
            }
        }

    fun takePicture(context: Context, action: (Path) -> Unit) {
        val imageCapture = imageCapture ?: return
        onImageReady = action
        imageCapture.takePicture(context.executor, this)
    }

    private val Context.executor: Executor
        get() = ContextCompat.getMainExecutor(this)

    override fun onCaptureSuccess(image: ImageProxy) {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        val exifInterface =
            androidx.exifinterface.media.ExifInterface(ByteArrayInputStream(bytes))
        val rotation = exifInterface.rotationDegrees
        val bitmap = CameraImageProcessExtension.resizeBitmap(
            BitmapFactory.decodeByteArray(
                bytes,
                0,
                bytes.size
            ), rotation
        )
        val path = CameraImageProcessExtension.generateImageFile(bitmap)
        onImageReady.invoke(path)
    }

    override fun onError(exception: ImageCaptureException) {

    }
}
