package com.example.galleryfacedetection.data.dao

import androidx.room.*
import com.example.galleryfacedetection.domain.TaggedImage

@Dao
interface TaggedImageDao {
    @Query("SELECT * FROM tagged_images WHERE imageUri = :imageUri")
    suspend fun getTagsForImage(imageUri: String): TaggedImage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTag(taggedImage: TaggedImage)

    @Query("DELETE FROM tagged_images WHERE imageUri = :imageUri")
    suspend fun deleteTagsForImage(imageUri: String)
}