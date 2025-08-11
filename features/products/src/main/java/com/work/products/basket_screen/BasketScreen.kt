package com.work.products.basket_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.work.base.compose.component.PrimaryButton
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Grey40
import com.work.base.compose.theme.Red
import com.work.base.extension.toPriceString
import com.work.stores_service.data.model.entity.BasketItemEntity
import org.koin.androidx.compose.koinViewModel

@Composable
fun BasketScreen(
    viewModel: BasketScreenViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    onCheckout: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    BasketScreen(
        uiState = uiState.value,
        onBack = onBack,
        onCheckout = onCheckout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketScreen(
    uiState: BasketScreenViewModel.UIState,
    onBack: () -> Unit = {},
    onCheckout: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Basket",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }
                },
                windowInsets = TopAppBarDefaults.windowInsets,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            )
        },
        bottomBar = {
            BasketScreenBottomBar(
                uiState = uiState,
                onCheckout = onCheckout
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
                .fillMaxSize()
        ) {
            items(uiState.orderList) { order ->
                BasketScreenItem(
                    item = order,
                )
            }
        }
    }
}

@Composable
fun BasketScreenItem(
    item:  BasketItemEntity
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier.size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    model = item.imageUrl,
                    contentDescription = "Menu Image",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = com.work.design.R.drawable.placeholder)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${item.quantity} Packs",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Text(
                text = "฿${(item.price * item.quantity).toPriceString()}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(
            modifier = Modifier.fillMaxWidth()
                .height(1.dp)
                .background(Grey40)
        )
    }
}

@Composable
fun BasketScreenBottomBar(
    uiState: BasketScreenViewModel.UIState,
    onCheckout: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(Red)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "฿ ${uiState.totalPrice}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        PrimaryButton(
            modifier = Modifier.width(200.dp),
            text = "Checkout",
            onClick = onCheckout
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BasketScreenItemPreview() {
    EMarketTheme {
        BasketScreenItem(
            item = BasketItemEntity(
                productName = "Super Sald",
                price = 1000,
                imageUrl = "",
                quantity = 2
            )
        )
    }
}

@Preview
@Composable
fun BasketScreenPreview() {
    EMarketTheme {
        BasketScreen(
            uiState = BasketScreenViewModel.UIState(
                orderList = listOf(
                    BasketItemEntity(
                        productName = "Super Sald",
                        price = 1000,
                        imageUrl = "",
                        quantity = 2
                    ),
                    BasketItemEntity(
                        productName = "Super Sald",
                        price = 1000,
                        imageUrl = "",
                        quantity = 2
                    ),
                    BasketItemEntity(
                        productName = "Super Sald",
                        price = 1000,
                        imageUrl = "",
                        quantity = 2
                    )
                ),
                totalPrice = "1,000",
                address = "서우"
            )
        )
    }
}