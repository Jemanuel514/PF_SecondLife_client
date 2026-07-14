package com.example.pf_secondlife_client.ui.user.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginFormView(
    onNavigateToRegister: () -> Unit,
    viewModel: LoginFormViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val isSubmitting = uiState is LoginFormModel.Submitting

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = loginState.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text(text = "Username") },
            isError = loginState.usernameError != null,
            supportingText = { loginState.usernameError?.let { Text(text = it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = loginState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text(text = "Password") },
            isError = loginState.passwordError != null,
            supportingText = { loginState.passwordError?.let { Text(text = it) } },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiState is LoginFormModel.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as LoginFormModel.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = viewModel::submit,
            enabled = loginState.isValid() && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(text = "Don't have an account yet? Register")
        }
    }
}