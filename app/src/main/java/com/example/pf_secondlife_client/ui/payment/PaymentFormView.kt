package com.example.pf_secondlife_client.ui.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pf_secondlife_client.domain.valuesSets.CardType
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardTypeDropdown(
    selected: CardType?,
    onSelected: (CardType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Card type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            CardType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(text = type.name) },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun PaymentForm(
    formState: PaymentModel,
    isSubmitting: Boolean,
    onCardTypeChange: (CardType) -> Unit,
    onCardholderNameChange: (String) -> Unit,
    onCardNumberChange: (String) -> Unit,
    onExpirationChange: (String) -> Unit,
    onCvvChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CardTypeDropdown(
            selected = formState.cardType,
            onSelected = onCardTypeChange,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.cardholderName,
            onValueChange = onCardholderNameChange,
            label = { Text(text = "Cardholder name") },
            isError = formState.cardholderNameError != null,
            supportingText = { formState.cardholderNameError?.let { Text(text = it) } },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.cardNumber,
            onValueChange = onCardNumberChange,
            label = { Text(text = "Card number") },
            isError = formState.cardNumberError != null,
            supportingText = { formState.cardNumberError?.let { Text(text = it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.expiration,
                onValueChange = onExpirationChange,
                label = { Text("MM/YY") },
                isError = formState.expirationError != null,
                supportingText = { formState.expirationError?.let { Text(it) } },
                visualTransformation = ExpirationVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = formState.cvv,
                onValueChange = onCvvChange,
                label = { Text(text = "CVV") },
                isError = formState.cvvError != null,
                supportingText = { formState.cvvError?.let { Text(text = it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            enabled = formState.isValid() && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(text = "Pay")
            }
        }
    }
}

@Composable
private fun LaunchedEffectOnce(effect: () -> Unit) {
    LaunchedEffect(Unit) { effect() }
}

@Composable
fun PaymentFormView(
    postId: String,
    onPaymentSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: PaymentFormViewModel = koinViewModel(parameters = { parametersOf(postId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()

    when (val state = uiState) {
        is PaymentFormModel.Locking -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is PaymentFormModel.LockFailed -> Unit

        is PaymentFormModel.Success -> {
            AlertDialog(
                onDismissRequest = onPaymentSuccess,
                confirmButton = {
                    TextButton(onClick = onPaymentSuccess) { Text("Accept") }
                },
                title = { Text("Payment successful") },
                text = { Text("Your purchase was processed successfully.") },
            )
        }

        is PaymentFormModel.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = state.message)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCancel) { Text(text = "Go back") }
            }
        }

        is PaymentFormModel.Ready, is PaymentFormModel.Submitting -> {
            PaymentForm(
                formState = paymentState,
                isSubmitting = uiState is PaymentFormModel.Submitting,
                onCardTypeChange = viewModel::onCardTypeChange,
                onCardholderNameChange = viewModel::onCardholderNameChange,
                onCardNumberChange = viewModel::onCardNumberChange,
                onExpirationChange = viewModel::onExpirationChange,
                onCvvChange = viewModel::onCvvChange,
                onSubmit = viewModel::submitPayment,
            )
        }
    }
}