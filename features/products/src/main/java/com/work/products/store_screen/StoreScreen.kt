package com.work.products.store_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Green40
import com.work.base.compose.theme.Grey40
import com.work.base.compose.theme.White
import kotlin.math.E

@Composable
fun StoreScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(White)
        ) {
            item(key = 1, contentType = "header") {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Green40)
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(
                                White,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            item(key = 2, contentType = "subHeader") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "The Coffee Shop",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(12.dp),
                            imageVector = Icons.Filled.Star,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = "4.8",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        Icon(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(com.work.design.R.drawable.store),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = "15.00 - 20.00",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item(key = 3, contentType = "menuHeader") {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)

                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Grey40)
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        text = "Menu",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item(key = 4, contentType = "menuItems") {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                }
            }
        }
    }
}

@Preview
@Composable
fun StoreScreenPreview() {
    EMarketTheme {
        StoreScreen()
    }
}