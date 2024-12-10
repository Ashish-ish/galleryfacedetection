package com.ashish.galleryfacedetection.ui.components.views

import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ashish.galleryfacedetection.ui.components.BoundingBox
import com.ashish.galleryfacedetection.util.Constants

@Composable
fun ImageWithBoundingBoxes(
    imageUri: Uri,
    faces: List<Rect>,
    tag: String?,
    originalImageWidth: Int,
    originalImageHeight: Int,
    onTag: (String) -> Unit
) {
    val taggingState = remember { mutableStateOf<Rect?>(null) }
    val tagName = remember { mutableStateOf(tag ?: "") }
    val taggedFaces = remember { mutableStateListOf<Pair<Rect, String>>() }

    LaunchedEffect(tag) {
        tag?.let {
            if (tag.isNotEmpty())
                faces.forEach { face -> taggedFaces.add(face to it) }
        }
    }

    val displayWidth = LocalContext.current.resources.displayMetrics.widthPixels / 3f
    val boxHeight = displayWidth*4/3
    val displayHeight = boxHeight.dp.value
    val density = LocalContext.current.resources.displayMetrics.density

    Box(
        modifier = Modifier
            .height((boxHeight/density).dp)
            .width((displayWidth / density).dp)
            .background(Color.LightGray)
    ) {
        OptimizedImage(imageUri, (boxHeight).toInt(), (displayWidth).toInt())
        Log.d("imageswithboxes===",""+imageUri)
        faces.forEach { face ->
            val scaledBox = scaleRectForGridCell(face, originalImageWidth, originalImageHeight, displayWidth, displayHeight)
            BoundingBox(scaledBox, density) { taggingState.value = face }
        }

        taggedFaces.forEach { (rect, tag) ->
            val scaledBox = scaleRectForGridCell(rect, originalImageWidth, originalImageHeight, displayWidth, displayHeight)
            TagTooltip(scaledBox, tag, density)
        }

        taggingState.value?.let { selectedFace ->
            CompactTextInput(
                value = tagName.value,
                onValueChange = { tagName.value = it },
                placeholder = Constants.TAG_PLACEHOLDER,
                onDone = {
                    if (tagName.value.isNotBlank()) {
                        onTag(tagName.value)
                        taggedFaces.add(selectedFace to tagName.value)
                        taggingState.value = null
                        tagName.value = ""
                    }
                },
                modifier = Modifier
                    .offset(
                        y = 10.dp
                    )
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            )
        }
    }
}

fun scaleRectForGridCell(
    rect: Rect,
    originalWidth: Int,
    originalHeight: Int,
    cellWidth: Float,
    cellHeight: Float
): Rect {
    val scaleX = cellWidth / originalWidth
    val scaleY = cellHeight / originalHeight
    val left = (rect.left * scaleX).toInt()
    val top = (rect.top * scaleY).toInt()
    val right = (rect.right * scaleX).toInt()
    val bottom = (rect.bottom * scaleY).toInt()

    return Rect(left, top, right, bottom)
}
