package com.example.galleryfacedetection.ui.components

import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ImageWithBoundingBoxes(
    imageUri: Uri,
    faces: List<Rect>,
    originalImageWidth: Int,
    originalImageHeight: Int,
    onTag: (Rect, String) -> Unit
) {
    val taggingState = remember { mutableStateOf<Rect?>(null) }
    val tagName = remember { mutableStateOf("") }
    val taggedFaces = remember { mutableStateListOf<Pair<Rect, String>>() } // Store tagged faces with tags

    // Calculate display dimensions for each grid cell
    val displayWidth = LocalContext.current.resources.displayMetrics.widthPixels / 3f
    val displayHeight = 200.dp.value // Convert dp to pixels for calculations

    Box(
        modifier = Modifier
            .height(200.dp)
            .width((displayWidth / LocalContext.current.resources.displayMetrics.density).dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        // Display the image
        OptimizedImage(imageUri)

        // Draw bounding boxes
        faces.forEach { face ->
            val scaledBox = scaleRectForGridCell(
                rect = face,
                originalWidth = originalImageWidth,
                originalHeight = originalImageHeight,
                cellWidth = displayWidth,
                cellHeight = displayHeight
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (scaledBox.left / LocalContext.current.resources.displayMetrics.density).dp,
                        y = (scaledBox.top).dp
                    )
                    .size(
                        width = ((scaledBox.right - scaledBox.left) / LocalContext.current.resources.displayMetrics.density).dp,
                        height = ((scaledBox.bottom - scaledBox.top)).dp
                    )
                    .border(2.dp, Color.Red)
                    .clickable { taggingState.value = face }
            )
        }

        // Display tooltips for tagged faces
        taggedFaces.forEach { (rect, tag) ->
            val scaledBox = scaleRectForGridCell(
                rect = rect,
                originalWidth = originalImageWidth,
                originalHeight = originalImageHeight,
                cellWidth = displayWidth,
                cellHeight = displayHeight
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (scaledBox.left / LocalContext.current.resources.displayMetrics.density).dp,
                        y = ((scaledBox.top).dp - 30.dp) // Position tooltip above bounding box
                    )
                    .background(Color.Black, RoundedCornerShape(4.dp))
                    .padding(1.dp)
                    .width(
                        ((scaledBox.right - scaledBox.left) * displayWidth / (originalImageWidth)).dp
                    )
            ) {
                Text(
                    text = tag,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Transparent EditText for tagging
        taggingState.value?.let { selectedFace ->
            CompactTextInput(
                value = tagName.value,
                onValueChange = { tagName.value = it },
                placeholder = "Tag this face",
                onDone = {
                    if (tagName.value.isNotBlank()) {
                        onTag(selectedFace, tagName.value)
                        taggedFaces.add(selectedFace to tagName.value) // Add to tagged faces
                        taggingState.value = null // Clear tagging state
                        tagName.value = "" // Clear text input
                    }
                },
                modifier = Modifier
                    .offset(
                        x = 10.dp,
                        y = ((selectedFace.top * displayHeight / originalImageHeight) / LocalContext.current.resources.displayMetrics.density).dp
                    )
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
    // Calculate scale factors for the grid cell
    val scaleX = cellWidth / originalWidth
    val scaleY = cellHeight / originalHeight
    // Scale the bounding box coordinates
    val left = (rect.left * scaleX).toInt()
    val top = (rect.top * scaleY).toInt()
    val right = (rect.right * scaleX).toInt()
    val bottom = (rect.bottom * scaleY).toInt()

    // Ensure the bounding box stays within the grid cell
    return Rect(left, top, right, bottom)
}
