package com.work.emarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.work.base.compose.theme.EMarketTheme
import com.work.base.navigation.Route
import com.work.products.screen.basket.BasketScreen
import com.work.products.screen.confirm.ConfirmOrderScreen
import com.work.products.screen.store.StoreScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EMarketTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Route.StoreScreen,
                    enterTransition = {
                        slideInHorizontally(initialOffsetX = { -it })
                    },
                    exitTransition = {
                        slideOutHorizontally(targetOffsetX = { -it })
                    }
                ) {
                    composable<Route.StoreScreen> {
                        StoreScreen(
                            onNavigateToBasket = {
                                navController.navigate(Route.BasketScreen) {
                                    // Navigate to Basket Screen that add store screen to backstack

                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable<Route.BasketScreen> {
                        BasketScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onSubmit = {
                                // I want to navigate to complete order screen by sending address to it
                                navController.navigate(Route.SuccessScreen(it))
                            }
                        )
                    }
                    composable<Route.SuccessScreen> {
                        ConfirmOrderScreen(
                            onDone = {
                                navController.popBackStack(Route.StoreScreen, false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EMarketTheme {
        Greeting("Android")
    }
}