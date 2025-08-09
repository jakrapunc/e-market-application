package com.work.base.compose.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Orange
import com.work.base.compose.theme.Orange40
import com.work.base.compose.theme.Red

@Composable
fun SubtractButton(
    buttonSize: Dp = 36.dp,
    animateScaleSize: Float = 1.2f,
    stiffness: Float = 600f,
    onClick: () -> Unit = {}
) {
    val scale = remember { Animatable(1f) }
    var triggerAnimation by remember { mutableIntStateOf(0) }

    LaunchedEffect(triggerAnimation) {
        if (triggerAnimation > 0) {
            scale.animateTo(
                targetValue = animateScaleSize,
                animationSpec = spring(stiffness = stiffness)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(stiffness = stiffness)
            )
        }
    }

    IconButton(
        onClick = {
            triggerAnimation++
            onClick()
        },
        modifier = Modifier
            .size(buttonSize)
            .scale(scale.value),
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Box(
            modifier = Modifier.size(buttonSize)
                .clip(CircleShape)
                .background(Orange40),
            contentAlignment = Alignment.Center
        ) {
            Spacer(
                modifier = Modifier.size(width = (buttonSize.value / 2).dp, height = 1.5.dp)
                    .background(Orange)
            )
        }
    }
}

@Preview
@Composable
fun SubtractButtonPreview() {
    EMarketTheme {
        SubtractButton()
    }
}