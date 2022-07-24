package com.saraiva.felipe.mycamera.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraVideoModel @Inject constructor(): ViewModel() {

    var imageFilePicture: String? = null

}