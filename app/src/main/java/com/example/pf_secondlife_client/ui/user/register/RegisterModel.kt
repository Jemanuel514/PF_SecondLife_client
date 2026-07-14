package com.example.pf_secondlife_client.ui.user.register

data class RegisterModel(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
) {
    fun isValid(): Boolean {
        return username.isNotBlank()        && usernameError == null &&
               password.isNotBlank()        && passwordError == null &&
               email.isNotBlank()           && emailError == null &&
               confirmPassword.isNotBlank() && confirmPasswordError == null
    }
}