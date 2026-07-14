package com.example.pf_secondlife_client.ui.user.register

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
fun RegisterFormView(
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterFormViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val registerState by viewModel.registerState.collectAsState()
    val isSubmitting = uiState is RegisterFormModel.Submitting

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Create account", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = registerState.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text(text = "Username") },
            isError = registerState.usernameError != null,
            supportingText = { registerState.usernameError?.let { Text(text = it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = registerState.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text(text = "Email") },
            isError = registerState.emailError != null,
            supportingText = { registerState.emailError?.let { Text(text = it) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = registerState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text(text = "Password") },
            isError = registerState.passwordError != null,
            supportingText = { registerState.passwordError?.let { Text(text = it) } },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = registerState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text(text = "Confirm password") },
            isError = registerState.confirmPasswordError != null,
            supportingText = { registerState.confirmPasswordError?.let { Text(text = it) } },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiState is RegisterFormModel.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as RegisterFormModel.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = viewModel::submit,
            enabled = registerState.isValid() && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(text = "Register")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(text = "Already have an account? Login")
        }
    }
}