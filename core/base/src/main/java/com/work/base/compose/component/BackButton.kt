package com.work.base.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.work.base.compose.theme.EMarketTheme

@Composable
fun BackButton(
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.size(width = 80.dp, height = 32.dp)
            .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.rotate(90f),
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = "Go Back",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Preview
@Composable
fun BackButtonPreview() {
    EMarketTheme {
        BackButton()
    }
}