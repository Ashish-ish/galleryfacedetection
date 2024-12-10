package com.example.galleryfacedetection.ui.components

import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.galleryfacedetection.domain.ImageData

@Composable
fun GalleryGrid(
    galleryImages: List<ImageData>,
    loadingMore: Boolean,
    gridState: LazyGridState,
    onTag: (Uri, String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(galleryImages.size) { galleryIndex ->
            val imageData = galleryImages[galleryIndex]
            val imageFaces = imageData.rect
            ImageWithBoundingBoxes(
                imageUri = imageData.uri,
                faces = imageFaces,
                originalImageHeight = imageData.imageHeight,
                originalImageWidth = imageData.imageWidth,
                tag = imageData.tag,
                onTag = { tag -> onTag(imageData.uri, tag) }
            )
        }

        if (loadingMore) {
            item {
                LoadingScreen()
            }
        }
    }
}