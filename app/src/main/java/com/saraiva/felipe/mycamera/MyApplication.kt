package com.saraiva.felipe.mycamera

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class MyApplication: Application() , CameraXConfig.Provider {

    @SuppressLint("UnsafeOptInUsageError")
    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR).build()
    }

    override fun onCreate() {
        super.onCreate()
    }
}