package com.example.galleryfacedetection.ui.components

import android.graphics.Rect
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ImageWithBoundingBoxes(
    imageUri: Uri,
    faces: List<Rect>,
    onTag: (Rect, String) -> Unit
) {
    val taggingState = remember { mutableStateOf<Rect?>(null) }
    val tagName = remember { mutableStateOf("") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null
        )

        faces.forEach { face ->
            Box(
                modifier = Modifier
                    .border(2.dp, Color.Red)
                    .offset(face.left.dp, face.top.dp)
                    .size(face.width().dp, face.height().dp)
                    .clickable { taggingState.value = face }
            )
        }

        taggingState.value?.let { selectedFace ->
            TextField(
                value = tagName.value,
                onValueChange = { tagName.value = it },
                label = { Text("Enter tag") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (tagName.value.isNotBlank()) {
                            onTag(selectedFace, tagName.value)
                            taggingState.value = null
                            tagName.value = ""
                        }
                    }
                ),
                singleLine = true
            )
        }
    }
}