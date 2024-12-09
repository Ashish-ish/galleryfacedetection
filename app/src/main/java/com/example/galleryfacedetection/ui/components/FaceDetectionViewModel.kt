package com.example.galleryfacedetection.ui.components

import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryfacedetection.data.repositories.FaceDetectionRepository
import com.example.galleryfacedetection.data.repositories.GalleryRepository
import com.example.galleryfacedetection.data.repositories.TaggingRepository
import com.example.galleryfacedetection.domain.ImageData
import com.example.galleryfacedetection.domain.TaggedFace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val faceDetectionRepository: FaceDetectionRepository,
    private val taggingRepository: TaggingRepository
) : ViewModel() {

    private val _galleryImages = MutableLiveData<List<ImageData>>()
    val galleryImages: LiveData<List<ImageData>> = _galleryImages

    private val _detectedFaces = MutableLiveData<Map<Uri, List<Rect>>>()
    val detectedFaces: LiveData<Map<Uri, List<Rect>>> = _detectedFaces

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingMore = MutableLiveData(true)
    val loadingMore: LiveData<Boolean> = _isLoadingMore
    val limit = 15
    var page = 1
    fun loadGalleryImages() {
        _isLoading.value = true
        if (page > 1) {
            _isLoadingMore.value = true
        }
        viewModelScope.launch(Dispatchers.IO) {
            val validImages = mutableListOf<ImageData>()
            val facesMap = mutableMapOf<Uri, List<Rect>>()
            while (validImages.size < limit) {
                val nextImages = galleryRepository.fetchGalleryImages(page) // Fetch limit images at a time
                page++
                if (nextImages.isEmpty()) break // Stop if no more images are available

                nextImages.forEach { imageUri ->
                    val bitmap = galleryRepository.loadBitmapFromUri(imageUri)
                    bitmap?.let {
                        val faces = faceDetectionRepository.detectFaces(bitmap)
                        if (faces.isNotEmpty()) {
                            facesMap[imageUri] = faces
                            val imageData = ImageData(imageUri, bitmap.height, bitmap.width)
                            validImages.add(imageData)
                        }
                    }
                    if (validImages.size == limit) return@forEach // Stop if we already have 10 valid images
                }
            }
            _galleryImages.value?.let {
                _isLoadingMore.postValue(false)
                _galleryImages.postValue(it + validImages)
            } ?: run {
                _galleryImages.postValue(validImages)
            }
            _detectedFaces.postValue(facesMap)
            _isLoading.postValue(false)
        }
    }

    fun tagFace(imageUri: Uri, rect: Rect, tag: String) {
        val existingTags = taggingRepository.getTags(imageUri)?.toMutableList() ?: mutableListOf()
        existingTags.add(TaggedFace(rect, tag))
        taggingRepository.addTags(imageUri, existingTags)
    }
}