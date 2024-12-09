package com.example.galleryfacedetection.data.repositories

import android.net.Uri
import com.example.galleryfacedetection.data.dao.TaggedImageDao
import com.example.galleryfacedetection.domain.TaggedFace
import com.example.galleryfacedetection.domain.TaggedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaggingRepository @Inject constructor(
    private val taggedImageDao: TaggedImageDao
) {
    suspend fun getTags(imageUri: String): List<String> {
        val taggedImage = withContext(Dispatchers.IO) {
            taggedImageDao.getTagsForImage(imageUri)
        }
        return taggedImage?.tags?.split(",") ?: emptyList()
    }

    suspend fun addTag(imageUri: String, tag: String) {
        withContext(Dispatchers.IO) {
            val existingTags = getTags(imageUri)
            val updatedTags = (existingTags + tag).distinct().joinToString(",")
            taggedImageDao.saveTag(TaggedImage(imageUri, updatedTags))
        }
    }

    suspend fun clearTags(imageUri: String) {
        withContext(Dispatchers.IO) {
            taggedImageDao.deleteTagsForImage(imageUri)
        }
    }
}