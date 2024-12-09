package com.example.galleryfacedetection.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle

@Composable
fun CompactTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    onDone: () -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 10.sp, // Compact font size
            color = Color.Black // Text color
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier
            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp), // Reduce padding here
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.padding(2.dp) // Adjust padding around the text field
            ) {
                if (value.isEmpty()) {
                    Text(text = placeholder, fontSize = 14.sp, color = Color.Gray)
                }
                innerTextField()
            }
        }
    )
}