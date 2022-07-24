package com.saraiva.felipe.mycamera.presentation.screens.preview

import androidx.compose.foundation.Image
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.saraiva.felipe.mycamera.presentation.navigation.FileInfo

@Composable
fun ViewerScreen(navController: NavHostController, image: String) {
    ConstraintLayout {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .size(Size.ORIGINAL) // Set the target size to load the image at.
                .build()
        )
        val view = createRef()
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> CircularProgressIndicator(modifier = Modifier.constrainAs(view) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.value(80.dp)
                height = Dimension.value(80.dp)
            })
            is AsyncImagePainter.State.Success -> Image(
                painter = painter,
                contentDescription = "content",
                modifier = Modifier.constrainAs(view) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.matchParent
                    height = Dimension.matchParent
                }
            )
            else -> Text(text = "Nao foi possivel carregar a imagem", modifier = Modifier.constrainAs(view) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.matchParent
                height = Dimension.wrapContent
            })
        }
    }
}