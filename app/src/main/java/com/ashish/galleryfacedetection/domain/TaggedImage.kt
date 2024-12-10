package com.ashish.galleryfacedetection.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tagged_images")
data class TaggedImage(
    @PrimaryKey val imageUri: String,
    val tags: String
)