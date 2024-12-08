package com.example.galleryfacedetection.data.repositories

import android.net.Uri
import com.example.galleryfacedetection.domain.TaggedFace
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaggingRepository @Inject constructor() {
    private val faceTags = mutableMapOf<Uri, List<TaggedFace>>()

    fun addTags(imageUri: Uri, taggedFaces: List<TaggedFace>) {
        faceTags[imageUri] = taggedFaces
    }

    fun getTags(imageUri: Uri): List<TaggedFace>? = faceTags[imageUri]
}