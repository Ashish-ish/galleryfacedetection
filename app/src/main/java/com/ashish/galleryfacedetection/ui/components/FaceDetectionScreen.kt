package com.ashish.galleryfacedetection.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashish.galleryfacedetection.ui.components.views.TopBar
import com.ashish.galleryfacedetection.util.Constants
import com.ashish.galleryfacedetection.util.Constants.CAMERA_GALLERY
import com.ashish.galleryfacedetection.util.Constants.GALLERY

@Composable
fun FaceDetectionScreen(viewModel: FaceDetectionViewModel = hiltViewModel()) {
    val galleryImages by viewModel.galleryImages.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(true)
    val loadingMore by viewModel.loadingMore.observeAsState(false)

    var selectedOption by remember { mutableStateOf(CAMERA_GALLERY) }
    val dropdownOptions = listOf(CAMERA_GALLERY, GALLERY)
    var dropdownExpanded by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = gridState.layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 3 && !loadingMore && !isLoading
        }
    }

    var hasPermission by remember { mutableStateOf(false) }

    PermissionHandler(onPermissionGranted = {
        hasPermission = true
        viewModel.resetPagination()
        viewModel.loadGalleryImages(fromCameraGallery = selectedOption == CAMERA_GALLERY)
    })

    LaunchedEffect(shouldLoadMore.value, hasPermission) {
        if (shouldLoadMore.value && hasPermission) {
            viewModel.loadGalleryImages(fromCameraGallery = selectedOption == CAMERA_GALLERY)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = Constants.TITLE_FACE_TAGGING,
                selectedOption = selectedOption,
                dropdownOptions = dropdownOptions,
                dropdownExpanded = dropdownExpanded,
                onOptionSelected = { option ->
                    selectedOption = option
                    dropdownExpanded = false
                    if (hasPermission) {
                        viewModel.resetPagination()
                        viewModel.loadGalleryImages(fromCameraGallery = option == CAMERA_GALLERY)
                    }
                },
                onDropdownToggle = { dropdownExpanded = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding(), start = 10.dp, end = 10.dp, bottom= 5.dp)) {
            if (!hasPermission) {
                PermissionDeniedUI()
            } else {
                when {
                    isLoading && galleryImages.isEmpty() -> LoadingScreen()
                    galleryImages.isEmpty() -> EmptyStateUI()
                    else -> {
                        GalleryGrid(
                            galleryImages = galleryImages,
                            loadingMore = loadingMore,
                            gridState = gridState,
                            onTag = { uri, tag -> viewModel.tagFace(uri, tag) }
                        )
                    }
                }
            }
        }
    }
}
