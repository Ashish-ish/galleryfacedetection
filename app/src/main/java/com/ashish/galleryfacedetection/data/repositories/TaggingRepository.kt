package com.ashish.galleryfacedetection.data.repositories

import com.ashish.galleryfacedetection.data.dao.TaggedImageDao
import com.ashish.galleryfacedetection.domain.TaggedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaggingRepository @Inject constructor(
    private val taggedImageDao: TaggedImageDao
) {
    suspend fun getTags(imageUri: String): String {
        val taggedImage = withContext(Dispatchers.IO) {
            taggedImageDao.getTagsForImage(imageUri)
        }
        return taggedImage?.tags.orEmpty()
    }

    suspend fun addTag(imageUri: String, tag: String) {
        withContext(Dispatchers.IO) {
            taggedImageDao.saveTag(TaggedImage(imageUri, tag))
        }
    }

    suspend fun clearTags(imageUri: String) {
        withContext(Dispatchers.IO) {
            taggedImageDao.deleteTagsForImage(imageUri)
        }
    }
}