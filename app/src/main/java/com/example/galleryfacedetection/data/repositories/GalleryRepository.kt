package com.example.galleryfacedetection.data.repositories

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun fetchGalleryImages(): List<Uri> {
        val images = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, null
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                images.add(uri)
            }
        }
        return images
    }

    fun loadBitmapFromUri(uri: Uri): Bitmap? {
        val contentResolver: ContentResolver = context.contentResolver
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Use ImageDecoder for newer devices
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                // Use MediaStore for older devices
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null // Return null if loading fails
        }
    }
}