package com.example.galleryfacedetection.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tagged_images")
data class TaggedImage(
    @PrimaryKey val imageUri: String, // Store the URI as a string
    val tags: String // Store tags as a comma-separated string
)