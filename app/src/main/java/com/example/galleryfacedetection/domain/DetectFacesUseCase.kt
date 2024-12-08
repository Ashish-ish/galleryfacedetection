package com.example.galleryfacedetection.domain

import android.graphics.Bitmap
import android.graphics.Rect
import com.example.galleryfacedetection.data.repositories.FaceDetectionRepository

class DetectFacesUseCase(private val faceDetectionRepository: FaceDetectionRepository) {
    fun execute(image: Bitmap): List<Rect> {
        return faceDetectionRepository.detectFaces(image)
    }
}