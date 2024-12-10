package com.ashish.galleryfacedetection.data.repositories

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val limit = 10

    fun fetchGalleryImages(page: Int = 1, fromCameraRoll: Boolean = false): List<Uri> {
        val images = mutableListOf<Uri>()

        val projection = arrayOf(MediaStore.Images.Media._ID)

        val selection = if (fromCameraRoll) {
            "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        } else {
            null
        }

        val selectionArgs = if (fromCameraRoll) {
            arrayOf("Camera")
        } else {
            null
        }

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, "DATE_MODIFIED DESC"
        )

        val oldCounter = (page - 1) * limit

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            it.moveToPosition(oldCounter)
            while (it.moveToNext() && images.size < limit) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                images.add(uri)
            }
        }
        return images
    }

    suspend fun loadBitmapFromUri(uri: Uri, targetWidth: Int = 300, targetHeight: Int = 300): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.setTargetSize(targetWidth, targetHeight)
                    }
                } else {
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                        context.contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it, null, this)
                        }
                        inSampleSize = calculateInSampleSize(this, targetWidth, targetHeight)
                        inJustDecodeBounds = false
                    }
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, options)
                    }
                }

                bitmap?.let { ensureBitmapConfig(it) }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun ensureBitmapConfig(bitmap: Bitmap): Bitmap {
        return if (bitmap.config == Bitmap.Config.ARGB_8888) {
            bitmap
        } else {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}