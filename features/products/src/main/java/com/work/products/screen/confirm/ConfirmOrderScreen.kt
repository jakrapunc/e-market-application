package com.work.products.screen.confirm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.work.base.compose.component.PrimaryButton
import com.work.base.compose.theme.EMarketTheme

@Composable
fun ConfirmOrderScreen(
    onDone: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.size(100.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(164.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Congratulations !!!!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.size(14.dp))
        Text(
            text = "Your order has been confirmed",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.size(100.dp))
        PrimaryButton(
            modifier = Modifier.width(250.dp)
                .padding(horizontal = 20.dp),
            text = "Done",
            onClick = onDone
        )
    }
}

@Composable
fun ConfirmOrderScreenLoading() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(100.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(164.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Submitting your order",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun ConfirmOrderScreenError() {

}

@Composable
fun ConfirmOrderScreenSuccess(
    onDone: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(164.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Congratulations !!!!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.size(14.dp))
        Text(
            text = "Your order has been confirmed",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.size(100.dp))
        PrimaryButton(
            modifier = Modifier.width(250.dp)
                .padding(horizontal = 20.dp),
            text = "Done",
            onClick = onDone
        )
    }
}

@Preview
@Composable
fun ConfirmOrderScreenPreview() {
    EMarketTheme {
        ConfirmOrderScreen()
    }
}