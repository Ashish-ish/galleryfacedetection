package com.example.galleryfacedetection.ui.components


import android.graphics.Rect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TagTooltip(scaledBox: Rect, tag: String, density: Float) {
    Box(
        modifier = Modifier
            .offset(
                y = ((scaledBox.top/density).dp - 35.dp)
            )
            .clipToBounds()
            .fillMaxWidth()
            .background(Color.Black, RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Text(
            text = tag,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}