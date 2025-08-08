package com.work.base.compose.component

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Grey40
import com.work.base.compose.theme.Purple80

@Composable
fun NumberInput(
    modifier: Modifier = Modifier,
    number: String = "1",
    maxValue: Int = 99,
    onChange: (String) -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier.width(width = 60.dp),
        value = number,
        onValueChange = {
            if (it.isEmpty()) {
                onChange("")
                return@OutlinedTextField
            }

            it.toIntOrNull()?.let { number ->
                if (number <= maxValue) {
                    onChange(number.toString())
                }
            }
        },
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Grey40,
            unfocusedContainerColor = Grey40,
            focusedTextColor = Purple80,
            unfocusedTextColor = Purple80
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, // Keyboard optimized for decimal numbers
            imeAction = ImeAction.Done
        ),
    )
}

@Preview
@Composable
fun NumberInputPreview() {
    EMarketTheme {
        var number by remember { mutableStateOf("1") }
        NumberInput(
            number = number,
            onChange = {
                number = it
            }
        )
    }
}