package com.example.galleryfacedetection.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    selectedOption: String,
    dropdownOptions: List<String>,
    dropdownExpanded: Boolean,
    onOptionSelected: (String) -> Unit,
    onDropdownToggle: (Boolean) -> Unit
) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
        actions = {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                TextButton(onClick = { onDropdownToggle(true) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = selectedOption)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Arrow"
                        )
                    }
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { onDropdownToggle(false) }
                ) {
                    dropdownOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { onOptionSelected(option) }
                        )
                    }
                }
            }
        }
    )
}