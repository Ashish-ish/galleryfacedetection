package com.example.galleryfacedetection.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceDetectionScreen(viewModel: FaceDetectionViewModel = hiltViewModel()) {
    val galleryImages by viewModel.galleryImages.observeAsState(emptyList())
    val faces by viewModel.detectedFaces.observeAsState(mapOf<Uri, List<android.graphics.Rect>>())
    val isLoading by viewModel.isLoading.observeAsState(true)
    val loadingMore by viewModel.loadingMore.observeAsState(false)

    // Spinner state
    var selectedOption by remember { mutableStateOf("Gallery") }
    val dropdownOptions = listOf("Gallery", "Camera Gallery")
    var dropdownExpanded by remember { mutableStateOf(false) }

    // LazyGridState for tracking scroll position
    val gridState = rememberLazyGridState()

    // Detect when the user scrolls near the end of the list
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = gridState.layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 3 && !loadingMore && !isLoading
        }
    }

    // Trigger loading based on spinner selection
    LaunchedEffect(selectedOption) {
        viewModel.resetPagination() // Reset pagination when selection changes
        when (selectedOption) {
            "Gallery" -> viewModel.loadGalleryImages(fromCameraGallery = false, reset = true)
            "Camera Gallery" -> viewModel.loadGalleryImages(fromCameraGallery = true, reset = true)
        }
    }

    // Trigger loading more when user scrolls near the end
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadGalleryImages(fromCameraGallery = selectedOption == "Camera Gallery")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Face Tagging", style = MaterialTheme.typography.titleMedium) },
                actions = {
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        TextButton(onClick = { dropdownExpanded = true }) {
                            Text(text = selectedOption)
                        }
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            dropdownOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedOption = option
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading && galleryImages.isEmpty() -> {
                    // Show loading screen
                    LoadingScreen()
                }
                galleryImages.isEmpty() -> {
                    // Show empty state when no data is available
                    EmptyStateUI()
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        state = gridState, // Attach LazyGridState for scroll tracking
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp),
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
                }
            }
        }
    }
}

@Composable
fun EmptyStateUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No images found. Please try a different option or check your gallery.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}