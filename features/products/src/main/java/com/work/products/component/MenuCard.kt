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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.work.base.compose.component.ItemCountLayout
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.White

@Composable
fun MenuCard(
    itemCount: Int = 0,
    price: String = "0",
    menuName: String = "",
    imageUrl: String = "",
    onPlusClick: () -> Unit = {},
    onMinusClick: () -> Unit = {},
) {
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
            AsyncImage(
                model = imageUrl,
                modifier = Modifier.fillMaxWidth()
                    .height(150.dp),
                contentDescription = "Menu Image",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = com.work.design.R.drawable.placeholder)
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = menuName,
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
                    text = price,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                ItemCountLayout(
                    value = itemCount,
                    onPlusClick = onPlusClick,
                    onMinusClick = onMinusClick,
                )
            }
        }
    }
}

@Preview
@Composable
fun MenuCardPreview() {
    EMarketTheme {
        var currentValue by remember { mutableIntStateOf(0) }
        MenuCard(
            itemCount = currentValue,
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