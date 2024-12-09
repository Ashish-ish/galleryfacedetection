package com.example.galleryfacedetection.data.repositories

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    var limit = 15
    fun fetchGalleryImages(page: Int = 1): List<Uri> {
        val images = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, "DATE_MODIFIED ASC"
        )
        val oldCounter = (page-1)*limit
        var counter = (page)*limit
        val nextPage = (page+1)*limit
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            it.moveToPosition(oldCounter)
            while (it.moveToNext() && counter>oldCounter && counter < nextPage) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                images.add(uri)
                counter--
            }
        }
        return images
    }

    fun loadBitmapFromUri(uri: Uri): Bitmap? {
        val contentResolver: ContentResolver = context.contentResolver
        val bitmap = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        bitmap?.let {
          return ensureBitmapConfig(it)
        }
        return bitmap
    }

    fun ensureBitmapConfig(bitmap: Bitmap): Bitmap {
        // Check if the Bitmap is already in ARGB_8888 format
        return if (bitmap.config == Bitmap.Config.ARGB_8888) {
            bitmap
        } else {
            // Convert the Bitmap to ARGB_8888
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
    }
}