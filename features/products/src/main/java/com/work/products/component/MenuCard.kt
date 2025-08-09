package com.work.products.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.work.base.compose.component.AddButton
import com.work.base.compose.component.NumberInput
import com.work.base.compose.component.SubtractButton
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Orange40
import com.work.base.compose.theme.White

@Composable
fun MenuCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        shape = RectangleShape,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(
                modifier = Modifier.fillMaxWidth()
                    .height(150.dp)
                .background(MaterialTheme.colorScheme.error, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Honey lime",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.size(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "2,000",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Row(
                    modifier = Modifier.background(
                        color = Orange40,
                        shape = RoundedCornerShape(16.dp)
                    ),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubtractButton(buttonSize = 24.dp)
                    NumberInput(
                        modifier = Modifier.size(width = 24.dp, height = 24.dp),
                        fontSize = 14.sp
                    )
                    AddButton(buttonSize = 24.dp)
                }
            }
        }
    }
}

@Preview
@Composable
fun MenuCardPreview() {
    EMarketTheme {
        MenuCard()
    }
}