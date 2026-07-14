package com.example.pf_secondlife_client.ui.user.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pf_secondlife_client.data.users.UsersRepository
import com.example.pf_secondlife_client.domain.models.PostLoginRequest
import com.example.pf_secondlife_client.domain.models.PostRegisterRequest
import com.example.pf_secondlife_client.network.api.errorHandling.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

class RegisterFormViewModel(
    private val usersRepository: UsersRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterFormModel>(RegisterFormModel.Idle)
    val uiState: StateFlow<RegisterFormModel> = _uiState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterModel())
    val registerState: StateFlow<RegisterModel> = _registerState.asStateFlow()

    fun onUsernameChange(value: String) {
        _registerState.value = _registerState.value.copy(username = value, usernameError = null)
    }

    fun onEmailChange(value: String) {
        val error = if (value.isNotBlank() && !EMAIL_REGEX.matches(value)) "Invalid email" else null
        _registerState.value = _registerState.value.copy(email = value, emailError = error)
    }

    fun onPasswordChange(value: String) {
        val error = if (value.isNotBlank() && value.length < 8) "At least 8 characters" else null
        val confirmError = confirmPasswordErrorFor(value, _registerState.value.confirmPassword)
        _registerState.value = _registerState.value.copy(
            password = value,
            passwordError = error,
            confirmPasswordError = confirmError,
        )
    }

    fun onConfirmPasswordChange(value: String) {
        val error = confirmPasswordErrorFor(_registerState.value.password, value)
        _registerState.value = _registerState.value.copy(confirmPassword = value, confirmPasswordError = error)
    }

    private fun confirmPasswordErrorFor(password: String, confirm: String): String? =
        if (confirm.isNotBlank() && confirm != password) "The passwords don't match" else null

    fun submit() {
        val form = _registerState.value
        if (!form.isValid()) return

        viewModelScope.launch {
            _uiState.value = RegisterFormModel.Submitting
            usersRepository.register(PostRegisterRequest(form.username, form.email, form.password))
                .onSuccess {
                    usersRepository.login(PostLoginRequest(form.username, form.password))
                        .onSuccess {
                            _uiState.value = RegisterFormModel.Idle
                        }
                        .onFailure {
                            _uiState.value = RegisterFormModel.Error(
                                "Account created. Login to continue."
                            )
                        }
                }
                .onFailure { error ->
                    val message = when (error) {
                        is ApiException.Conflict -> "Username or email already in use"
                        is ApiException.Network -> "Not connected. Check your internet connection."
                        else -> error.message ?: "Couldn't finish the registration process. Try again."
                    }
                    _uiState.value = RegisterFormModel.Error(message)
                }
        }
    }
}