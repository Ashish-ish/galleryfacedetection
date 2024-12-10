package com.ashish.galleryfacedetection.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ashish.galleryfacedetection.data.dao.TaggedImageDao
import com.ashish.galleryfacedetection.domain.TaggedImage

@Database(entities = [TaggedImage::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taggedImageDao(): TaggedImageDao
}