package com.work.base.compose.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Grey40
import com.work.base.compose.theme.Grey80
import com.work.base.compose.theme.Purple80

@Composable
fun AddressInput(
    modifier: Modifier = Modifier,
    address: String = "",
    onChange: (String) -> Unit = {},
) {

    OutlinedTextField(
        modifier = modifier,
        value = address,
        onValueChange = {
            onChange(it)
        },
        minLines = 3,
        maxLines = 3,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Grey40,
            unfocusedContainerColor = Grey40,
            focusedTextColor = Purple80,
            unfocusedTextColor = Purple80
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = TextStyle(
            fontSize = 16.sp,
        ),
        placeholder = {
            Text(
                text = "Address",
                style = TextStyle(
                    fontSize = 16.sp,
                ),
                color = Grey80
            )
        },
    )
}

@Preview
@Composable
fun AddressInputPreview() {
    EMarketTheme {
        AddressInput()
    }
}