package com.example.galleryfacedetection.ui.components

import android.graphics.Rect
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galleryfacedetection.data.repositories.FaceDetectionRepository
import com.example.galleryfacedetection.data.repositories.GalleryRepository
import com.example.galleryfacedetection.data.repositories.TaggingRepository
import com.example.galleryfacedetection.domain.TaggedFace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val faceDetectionRepository: FaceDetectionRepository,
    private val taggingRepository: TaggingRepository
) : ViewModel() {

    private val _galleryImages = MutableLiveData<List<Uri>>()
    val galleryImages: LiveData<List<Uri>> = _galleryImages

    private val _detectedFaces = MutableLiveData<Map<Uri, List<Rect>>>()
    val detectedFaces: LiveData<Map<Uri, List<Rect>>> = _detectedFaces

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadGalleryImages() {
        _isLoading.value = true
        viewModelScope.launch {
            val images = galleryRepository.fetchGalleryImages()
            val facesMap = mutableMapOf<Uri, List<Rect>>()

            images.forEach { imageUri ->
                val bitmap = galleryRepository.loadBitmapFromUri(imageUri)
                bitmap?.let {
                    val faces = faceDetectionRepository.detectFaces(bitmap)
                    facesMap[imageUri] = faces
                }
            }

            _galleryImages.value = images
            _detectedFaces.value = facesMap
            _isLoading.value = false
        }
    }

    fun tagFace(imageUri: Uri, rect: Rect, tag: String) {
        val existingTags = taggingRepository.getTags(imageUri)?.toMutableList() ?: mutableListOf()
        existingTags.add(TaggedFace(rect, tag))
        taggingRepository.addTags(imageUri, existingTags)
    }

}