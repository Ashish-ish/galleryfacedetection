package com.example.galleryfacedetection.domain

import android.graphics.Rect
import android.net.Uri

data class ImageData(val uri: Uri, val imageHeight: Int, val imageWidth: Int, val tag: String?=null, val rect: List<Rect>)
