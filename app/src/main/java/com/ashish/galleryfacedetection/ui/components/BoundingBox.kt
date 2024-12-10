package com.ashish.galleryfacedetection.ui.components

import android.graphics.Rect
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BoundingBox(scaledBox: Rect, density: Float, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .offset(
                x = (scaledBox.left / density).dp,
                y = (scaledBox.top/density).dp
            )
            .size(
                width = ((scaledBox.right - scaledBox.left) / density).dp,
                height = ((scaledBox.bottom - scaledBox.top)/density).dp
            )
            .border(2.dp, Color.Red)
            .clickable { onClick() }
    )
}