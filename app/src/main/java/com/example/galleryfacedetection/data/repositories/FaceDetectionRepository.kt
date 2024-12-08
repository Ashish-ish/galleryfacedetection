package com.example.galleryfacedetection.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceDetectionRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val faceLandmarker: FaceLandmarker

    init {
        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(
                BaseOptions.builder()
                    .setModelAssetPath("facelandmarker.task") // Add this model in your assets directory
                    .build()
            )
            .setRunningMode(RunningMode.IMAGE)
            .setNumFaces(5) // Configure the maximum number of faces to detect
            .build()

        faceLandmarker = FaceLandmarker.createFromOptions(context, options)
    }
    
     fun detectFaces(image: Bitmap): List<Rect> {
         val mpImage = BitmapImageBuilder(image).build()
        
         val results: FaceLandmarkerResult = faceLandmarker.detect(mpImage)
        
         return results.faceLandmarks().mapNotNull { faceLandmarks ->
             val xs = faceLandmarks.map { it.x() }
             val ys = faceLandmarks.map { it.y() }
             val left = xs.minOrNull()?.toInt() ?: return@mapNotNull null
             val top = ys.minOrNull()?.toInt() ?: return@mapNotNull null
             val right = xs.maxOrNull()?.toInt() ?: return@mapNotNull null
             val bottom = ys.maxOrNull()?.toInt() ?: return@mapNotNull null
             Rect(left, top, right, bottom)
             }
         }
}