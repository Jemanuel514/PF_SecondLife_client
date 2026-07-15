package com.example.pf_secondlife_client.ui.user.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pf_secondlife_client.api.errorHandling.ApiException
import com.example.pf_secondlife_client.data.users.UsersRepository
import com.example.pf_secondlife_client.domain.models.PostLoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginFormViewModel(private val usersRepository: UsersRepository): ViewModel() {
    private val _uiState = MutableStateFlow<LoginFormModel>(LoginFormModel.Idle)
    val uiState: StateFlow<LoginFormModel> = _uiState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginModel())
    val loginState: StateFlow<LoginModel> = _loginState.asStateFlow()

    fun onUsernameChange(username: String) {
        _loginState.value = _loginState.value.copy(username = username, usernameError = null)
    }

    fun onPasswordChange(password: String) {
        _loginState.value = _loginState.value.copy(password = password, passwordError = null)
    }

    fun submit() {
        val login = _loginState.value
        if (!login.isValid()) return

        viewModelScope.launch {
            _uiState.value = LoginFormModel.Submitting
            usersRepository.login(PostLoginRequest(login.username, login.password))
                .onSuccess { _uiState.value = LoginFormModel.Idle }
                .onFailure { error->
                    val message = when (error) {
                        is ApiException.Unauthorized -> "Incorrect username or password."
                        is ApiException.Forbidden -> "Account deactivated."
                        is ApiException.Network -> "Not connected. Check your internet connection."
                        else -> error.message ?: "Couldn't finish the login process. Try again."
                    }
                    _uiState.value = LoginFormModel.Error(message)
                }
        }
    }
}