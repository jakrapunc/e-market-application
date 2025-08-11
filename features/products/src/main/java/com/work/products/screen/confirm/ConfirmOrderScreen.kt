package com.work.products.screen.confirm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.work.base.compose.component.PrimaryButton
import com.work.base.compose.theme.EMarketTheme
import com.work.base.compose.theme.Green40
import com.work.base.compose.theme.Green80
import com.work.base.compose.theme.White
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConfirmOrderScreen(
    viewModel: ConfirmOrderScreenViewModel = koinViewModel(),
    onDone: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ConfirmOrderScreen(
        uiState = uiState.value,
        onDone = {
            onDone()
        }
    )
}

@Composable
fun ConfirmOrderScreen(
    uiState: ConfirmOrderScreenViewModel.UIState,
    onDone: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (uiState.isLoading) {
            ConfirmOrderScreenLoading()
        } else if (uiState.isSuccess) {
            ConfirmOrderScreenSuccess(onDone = onDone)
        } else if (uiState.error != null) {
            ConfirmOrderScreenError()
        }
    }
}

@Composable
fun ConfirmOrderScreenLoading() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Box(
            modifier = Modifier.size(164.dp)
                .background(
                    color = Green40,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(100.dp)
                    .background(Green80, shape = CircleShape)
                    .padding(24.dp),
                imageVector = Icons.Filled.Done,
                contentDescription = "Success",
                tint = White
            )
        }
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
        ConfirmOrderScreen(
            uiState = ConfirmOrderScreenViewModel.UIState(
                isLoading = false,
                isSuccess = true
            ),
            onDone = {}
        )
    }
}