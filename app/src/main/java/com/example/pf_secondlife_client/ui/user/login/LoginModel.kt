package com.example.pf_secondlife_client.ui.user.login

data class LoginModel(
    val username: String = "",
    val password: String = "",

    val usernameError: String? = null,
    val passwordError: String? = null,
) {
    fun isValid(): Boolean {
        return username.isNotBlank() && usernameError == null &&
               password.isNotBlank() && passwordError == null
    }
}