package com.example.galleryfacedetection.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FaceDetectionScreen(viewModel: FaceDetectionViewModel = hiltViewModel()) {
    val galleryImages by viewModel.galleryImages.observeAsState(emptyList())
    val faces by viewModel.detectedFaces.observeAsState(mapOf<Uri, List<android.graphics.Rect>>())
    val isLoading by viewModel.isLoading.observeAsState(true)
    val loadingMore by viewModel.loadingMore.observeAsState(true)

    // Tracks whether a new page is being loaded

    PermissionHandler(onPermissionGranted = {
        viewModel.loadGalleryImages()
    })

    if (isLoading && galleryImages.isEmpty()) {
        LoadingScreen()
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display images
            items(galleryImages) { imageUri ->
                val imageFaces = faces[imageUri.uri] ?: emptyList()
                ImageWithBoundingBoxes(
                    imageUri = imageUri.uri,
                    faces = imageFaces,
                    originalImageHeight = imageUri.imageHeight,
                    originalImageWidth = imageUri.imageWidth,
                    onTag = { rect, tag ->
                        viewModel.tagFace(imageUri.uri, rect, tag)
                        Log.d("Tagging", "Tagged $rect with $tag")
                    }
                )
            }

            // Add progress bar as a footer
            if (loadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        // Detect when near the end of the grid and trigger paging
        LaunchedEffect(galleryImages) {
            if (viewModel.loadingMore.value == false && galleryImages.size >= 3) {
                // Check if we are near the end of the list
                if (viewModel.loadingMore.value == false) {
                    viewModel.loadGalleryImages() // Load more items
                }
            }
        }
    }
}