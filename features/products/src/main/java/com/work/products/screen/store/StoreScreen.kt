package com.work.products.screen.store

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.work.base.compose.component.BasketButton
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Grey40
import com.work.base.compose.theme.White
import com.work.base.extension.toPriceString
import com.work.design.R
import com.work.products.component.MenuCard
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoreScreen(
    viewModel: StoreScreenViewModel = koinViewModel(),
    onNavigateToBasket: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    StoreScreen(
        uiState = uiState.value,
        onEvent = viewModel::onEvent,
        onNavigateToBasket = onNavigateToBasket
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    uiState: StoreScreenViewModel.UIState,
    onEvent: (StoreScreenViewModel.UIEvent) -> Unit,
    onNavigateToBasket: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var headerAlpha by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember(lazyListState) {
        object: NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (lazyListState.firstVisibleItemIndex < 3) {
                    coroutineScope.launch {
                        lazyListState.scrollBy(-available.y)
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        snapshotFlow {
            when (lazyListState.firstVisibleItemIndex) {
                0 -> {
                    0f
                }
                1 -> {
                    lazyListState.firstVisibleItemScrollOffset.toFloat() / lazyListState.layoutInfo.visibleItemsInfo[1].size
                }
                else -> {
                    1f
                }
            }
        }.onEach {
            headerAlpha = it
        }.collect()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.alpha(headerAlpha),
                        text = uiState.storeInfo?.name ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                windowInsets = TopAppBarDefaults.windowInsets,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(
                        alpha = headerAlpha
                    )
                ),
            )
        },
        bottomBar = {
            AnimatedVisibility(
                uiState.totalItem > 0,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(White)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    BasketButton(
                        modifier = Modifier.fillMaxWidth(),
                        itemCount = uiState.totalItem,
                        totalPrice = uiState.totalPrice,
                        onClick = onNavigateToBasket
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(White),
            state = lazyListState
        ) {
            item(key = 0, contentType = "header") {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    AsyncImage(
                        model = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentDescription = "",
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
            item(key = 1, contentType = "subHeader") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = uiState.storeInfo?.name ?: "",
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
                            text = uiState.storeInfo?.rating?.toString() ?: "",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        Icon(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(R.drawable.store),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = "${uiState.storeInfo?.openingTime} - ${uiState.storeInfo?.closingTime}",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item(key = 2, contentType = "menuHeader") {
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

            item(key = 3, contentType = "menuItems") {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight()
                        .padding(horizontal = 20.dp)
                        .nestedScroll(nestedScrollConnection),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.products) { product ->
                        MenuCard(
                            itemCount = uiState.orderList.find { it.productName == product.name }?.quantity ?: 0,
                            price = product.price.toPriceString(),
                            menuName = product.name,
                            imageUrl = product.imageUrl,
                            onPlusClick = {
                                onEvent(StoreScreenViewModel.UIEvent.AddItem(product))
                            },
                            onMinusClick = {
                                onEvent(StoreScreenViewModel.UIEvent.RemoveItem(product))
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StoreScreenPreview() {
    EMarketTheme {
        StoreScreen(
            uiState = StoreScreenViewModel.UIState(),
            onEvent = {},
        )
    }
}