package com.work.base.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.White

@Composable
fun BasketButton(
    modifier: Modifier = Modifier,
    itemCount: Int = 0,
    totalPrice: String,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(4.dp))
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    text = itemCount.toString(),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "My basket",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp
                    ),
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "฿$totalPrice",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp
                ),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun BasketButtonPreview() {
    EMarketTheme {
        BasketButton(
            modifier = Modifier.width(300.dp),
            itemCount = 2,
            totalPrice = "1,000",
            onClick = {}
        )
    }
}
