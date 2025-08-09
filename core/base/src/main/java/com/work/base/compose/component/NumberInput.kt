package com.work.base.compose.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Purple80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInput(
    modifier: Modifier = Modifier,
    value: String = "1",
    maxValue: Int = 99,
    fontSize: TextUnit = 12.sp,
    onChange: (String) -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedTextColor = Purple80,
        unfocusedTextColor = Purple80,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )

    BasicTextField(
        value = value,
        onValueChange = {
            if (it.isEmpty()) {
                onChange("")
                return@BasicTextField
            }

            it.toIntOrNull()?.let { number ->
                if (number <= maxValue) {
                    onChange(number.toString())
                }
            }
        },
        singleLine = true,
        textStyle = TextStyle(
            fontSize = fontSize,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, // Keyboard optimized for decimal numbers
            imeAction = ImeAction.Done
        ),
        modifier = modifier,

    ) { decorationBox ->
        TextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = decorationBox,
            singleLine = true,
            contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                start = 0.dp,
                top = 0.dp,
                end = 0.dp,
                bottom = 0.dp
            ),
            colors = colors,
            enabled = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(size = 4.dp),

        )
    }
}

@Preview
@Composable
fun NumberInput2Preview() {
    EMarketTheme {
        NumberInput(
            modifier = Modifier
                .width(20.dp)
                .height(12.dp),
            fontSize = 10.sp
        )
    }
}
