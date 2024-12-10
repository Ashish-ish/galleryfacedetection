package com.ashish.galleryfacedetection.di

import android.content.Context
import com.ashish.galleryfacedetection.data.dao.TaggedImageDao
import com.ashish.galleryfacedetection.data.repositories.FaceDetectionRepository
import com.ashish.galleryfacedetection.data.repositories.GalleryRepository
import com.ashish.galleryfacedetection.data.repositories.TaggingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGalleryRepository(@ApplicationContext context: Context): GalleryRepository {
        return GalleryRepository(context)
    }

    @Provides
    @Singleton
    fun provideFaceDetectionRepository(@ApplicationContext context: Context): FaceDetectionRepository {
        return FaceDetectionRepository(context)
    }

    @Provides
    @Singleton
    fun provideTaggingRepository(taggedImageDao: TaggedImageDao): TaggingRepository {
        return TaggingRepository(taggedImageDao)
    }
}