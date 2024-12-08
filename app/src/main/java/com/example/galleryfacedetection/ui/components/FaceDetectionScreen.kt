package com.example.galleryfacedetection.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.facedetection.ui.components.ImageWithBoundingBoxes
import com.example.facedetection.ui.components.LoadingScreen
import com.example.facedetection.ui.components.PermissionHandler
import com.example.facedetection.viewmodel.FaceDetectionViewModel

@Composable
fun FaceDetectionScreen(viewModel: FaceDetectionViewModel = hiltViewModel()) {
    val galleryImages by viewModel.galleryImages.observeAsState(emptyList())
    val faces by viewModel.detectedFaces.observeAsState(mapOf<Uri, List<android.graphics.Rect>>())
    val isLoading by viewModel.isLoading.observeAsState(true)

    PermissionHandler(onPermissionGranted = {
        viewModel.loadGalleryImages()
    })

    if (isLoading) {
        LoadingScreen()
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(galleryImages) { imageUri ->
                val imageFaces = faces[imageUri] ?: emptyList()
                ImageWithBoundingBoxes(
                    imageUri = imageUri,
                    faces = imageFaces,
                    onTag = { rect, tag ->
                        viewModel.tagFace(imageUri, rect, tag)
                        Log.d("Tagging", "Tagged $rect with $tag")
                    }
                )
            }
        }
    }
}