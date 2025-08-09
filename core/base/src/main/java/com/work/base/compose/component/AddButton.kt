package com.work.base.compose.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Orange40

@Composable
fun AddButton(
    buttonSize: Dp = 36.dp,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .size(buttonSize),
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Icon(
            modifier = Modifier.size(buttonSize)
                .clip(CircleShape)
                .background(Orange40),
            imageVector = Icons.Filled.Add,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun AddButtonPreview() {
    EMarketTheme {
        AddButton()
    }
}