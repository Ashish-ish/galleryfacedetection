package com.ashish.galleryfacedetection.di

import android.content.Context
import androidx.room.Room
import com.ashish.galleryfacedetection.data.dao.TaggedImageDao
import com.ashish.galleryfacedetection.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gallery_face_detection_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaggedImageDao(database: AppDatabase): TaggedImageDao {
        return database.taggedImageDao()
    }
}