package com.work.base.compose.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Orange40

@Composable
fun ItemCountLayout(
    value: Int = 0,
    onPlusClick: () -> Unit = {},
    onMinusClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {}
) {
    val isExpanded = value > 0

    Row(
        modifier = Modifier.background(
            color = Orange40,
            shape = RoundedCornerShape(16.dp)
        ).animateContentSize(
            animationSpec = spring(
                stiffness = 200f
            )
        ),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isExpanded) {
            SubtractButton(
                onClick = onMinusClick,
                buttonSize = 24.dp
            )
            NumberInput(
                modifier = Modifier.size(width = 18.dp, height = 24.dp),
                fontSize = 14.sp,
                value = value.toString(),
                onChange = {
                    onValueChange(it)
                }
            )
        }
        AddButton(
            buttonSize = 24.dp,
            onClick = onPlusClick
        )
    }

}


@Preview
@Composable
fun ItemCountLayoutPreview() {
    EMarketTheme {
        var currentValue by remember { mutableIntStateOf(0) }
        ItemCountLayout(
            value = currentValue,
            onValueChange = {
                if (it.isEmpty()) {
                    currentValue = 0
                    return@ItemCountLayout
                }
                it.toIntOrNull()?.let { num -> currentValue = num }
            },
            onPlusClick = {
                currentValue++
            },
            onMinusClick = {
                if (currentValue >= 1) {
                    currentValue--
                }
            }
        )
    }
}