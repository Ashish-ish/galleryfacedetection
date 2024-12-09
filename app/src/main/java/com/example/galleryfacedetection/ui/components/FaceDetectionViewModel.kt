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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    private val _isLoadingMore = MutableLiveData(false)
    val loadingMore: LiveData<Boolean> = _isLoadingMore
    val limit = 15
    var page = 1

    fun resetPagination() {
        page = 1
        _galleryImages.postValue(emptyList())
        _detectedFaces.postValue(emptyMap())
    }

    fun loadGalleryImages(fromCameraGallery: Boolean = false, reset: Boolean = false) {

        if (page > 1) {
            _isLoadingMore.value = true
        } else {
            _isLoading.value = true
        }

        viewModelScope.launch(Dispatchers.IO) {

            val validImages = mutableListOf<ImageData>()
            val facesMap = mutableMapOf<Uri, List<Rect>>()
            while (validImages.size < limit) {
                val nextImages = galleryRepository.fetchGalleryImages(page, fromCameraGallery) // Fetch limit images at a time
                page++
                if (nextImages.isEmpty()) break // Stop if no more images are available
                val results = nextImages.map { imageUri ->
                    async {
                        val bitmap = galleryRepository.loadBitmapFromUri(imageUri)
                        bitmap?.let {
                            val faces = faceDetectionRepository.detectFaces(it)
                            if (faces.isNotEmpty()) {
                                Pair(imageUri, Pair(faces, bitmap))
                            } else null
                        }
                    }
                }.awaitAll()

                validImages.addAll(results.filterNotNull().map { (uri, data) ->
                    val (faces, bitmap) = data
                    facesMap[uri] = faces
                    ImageData(uri, bitmap.height, bitmap.width)
                })
                Log.d("validimages===",""+validImages.size)
            }
            Log.d("_galleryImages===","completed")
            _galleryImages.value?.let {
                _isLoadingMore.postValue(false)
                Log.d("_galleryImages===",""+_isLoadingMore)
                _galleryImages.postValue(it + validImages)
                _detectedFaces.value?.let {
                    _detectedFaces.postValue(it + facesMap)
                }

            } ?: run {
                Log.d("_galleryImages===","next")
                _galleryImages.postValue(validImages)
                _detectedFaces.postValue(facesMap)

            }
            _isLoading.postValue(false)
        }
    }

    fun tagFace(imageUri: Uri, rect: Rect, tag: String) {
        viewModelScope.launch {
            val existingTags = taggingRepository.getTags(imageUri.toString())?.toMutableList() ?: mutableListOf()
            existingTags.add(TaggedFace(rect, tag))
            taggingRepository.addTag(imageUri.toString(), existingTags)
        }

    }
}