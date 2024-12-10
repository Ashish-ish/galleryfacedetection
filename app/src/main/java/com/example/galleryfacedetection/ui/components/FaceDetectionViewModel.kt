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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    private val faceDetectionRepository: FaceDetectionRepository,
    private val taggingRepository: TaggingRepository
) : ViewModel() {

    private val _galleryImages = MutableLiveData<List<ImageData>>(emptyList())
    val galleryImages: LiveData<List<ImageData>> = _galleryImages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingMore = MutableLiveData(false)
    val loadingMore: LiveData<Boolean> = _isLoadingMore

    private val limit = 10
    private var page = 1
    private var isLastPage = false

    private val processedImagesCache = mutableMapOf<Uri, List<Rect>>()
    private var loadJob: Job? = null
    private val taggedDataCache = mutableMapOf<Uri, String>()


    fun resetPagination() {
        loadJob?.cancel()
        page = 1
        isLastPage = false
        processedImagesCache.clear()
        _galleryImages.value = emptyList()
        //_detectedFaces.value = emptyMap()
        _isLoading.postValue(true)
        _isLoadingMore.postValue(false)
    }

    fun loadGalleryImages(fromCameraGallery: Boolean = false) {
        if (isLastPage) return

        loadJob = viewModelScope.launch(Dispatchers.IO) {
            if (page > 1) {
                _isLoadingMore.postValue(true)
            } else {
                _isLoading.postValue(true)
            }

            try {
                val validImages = fetchValidImages(fromCameraGallery)
                appendGalleryImages(validImages)
            } catch (e: Exception) {
                Log.e("FaceDetectionViewModel", "Error loading images: ${e.message}")
            } finally {

                _isLoading.postValue(false)
                _isLoadingMore.postValue(false)
            }
        }
    }


    private suspend fun fetchValidImages(fromCameraGallery: Boolean): List<ImageData> {
        val validImages = mutableListOf<ImageData>()
        val semaphore = Semaphore(4)
        while (validImages.size < limit) {
            val nextImages = galleryRepository.fetchGalleryImages(page, fromCameraGallery)
            if (nextImages.isEmpty()) {
                isLastPage = true
                break
            }

            page++

            val results = coroutineScope {
                nextImages.map { imageUri ->
                    async(Dispatchers.IO) { semaphore.withPermit {
                        processImage(imageUri)
                    } }
                }.awaitAll()
            }

            results.filterNotNull().forEach { (uri, data) ->
                val (faces, bitmap) = data
                val tag = taggingRepository.getTags(uri.toString())
                validImages.add(ImageData(uri, bitmap.height, bitmap.width, tag, rect= faces))
            }
        }

        return validImages
    }

    private suspend fun processImage(imageUri: Uri): Pair<Uri, Pair<List<Rect>, android.graphics.Bitmap>>? {
        if (processedImagesCache.containsKey(imageUri)) {
            val cachedFaces = processedImagesCache[imageUri]
            return cachedFaces?.let { imageUri to (it to galleryRepository.loadBitmapFromUri(imageUri)!!) }
        }

        val bitmap = galleryRepository.loadBitmapFromUri(imageUri) ?: return null
        val faces = faceDetectionRepository.detectFaces(bitmap)
        if (faces.isNotEmpty()) {
            processedImagesCache[imageUri] = faces
            return imageUri to (faces to bitmap)
        }
        return null
    }

    private fun appendGalleryImages(newImages: List<ImageData>) {
        if (newImages.isEmpty()) {
            isLastPage = true
        }
        val updatedImages = _galleryImages.value.orEmpty() + newImages
        val mergedImages = updatedImages.map { image ->
            val tags = taggedDataCache[image.uri]
            tags?.let {
                image.copy(tag = tags)
            } ?: run {
                image
            }
        }

        _galleryImages.postValue(mergedImages)
    }

    fun tagFace(imageUri: Uri, tag: String) {
        viewModelScope.launch {
            try {
                taggingRepository.addTag(imageUri.toString(), tag)
                taggedDataCache[imageUri] = tag
            } catch (e: Exception) {
                Log.e("FaceDetectionViewModel", "Error tagging face: ${e.message}")
            }
        }
    }
}