package com.work.base.compose.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.work.base.compose.theme.EMarketTheme

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp)
        ),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 28.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.displaySmall.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview
@Composable
fun SecondaryButtonPreview() {
    EMarketTheme {
        SecondaryButton(
            modifier = Modifier.width(300.dp),
            text = "Shopping"
        )
    }
}