package com.example.galleryfacedetection.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
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
                    .setDelegate(Delegate.GPU)
                    .setModelAssetPath("face_landmarker.task")
                    .build()
            )
            .setRunningMode(RunningMode.IMAGE)
            .setNumFaces(1)
            .build()

        faceLandmarker = FaceLandmarker.createFromOptions(context, options)
    }
    
     fun detectFaces(image: Bitmap): List<Rect> {

         val mpImage = BitmapImageBuilder(image).build()
        
         val results: FaceLandmarkerResult = faceLandmarker.detect(mpImage)

         return results.faceLandmarks().mapNotNull { faceLandmarks ->
             val xs = faceLandmarks.map { it.x() }
             val ys = faceLandmarks.map { it.y() }
             val imageWidth = image.width
             val imageHeight = image.height

             val left = ((xs.minOrNull() ?: 0f) * imageWidth).toInt()
             val top = ((ys.minOrNull() ?: 0f) * imageHeight).toInt()
             val right = ((xs.maxOrNull() ?: 0f )* imageWidth).toInt()
             val bottom = ((ys.maxOrNull() ?: 0f) * imageHeight).toInt()

             Rect(left, top, right, bottom)
             }
         }
}