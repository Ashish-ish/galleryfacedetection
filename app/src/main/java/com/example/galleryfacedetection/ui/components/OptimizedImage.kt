package com.example.galleryfacedetection.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size

@Composable
fun OptimizedImage(imageUri: Uri, imageWidth: Int = 300, imageHeight: Int = 300) {
    androidx.compose.foundation.Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .size(Size(imageWidth, imageHeight)) // Downscale the image to a 300x300 resolution
                .build()
        ),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit // Adjust as per requirement
    )
}