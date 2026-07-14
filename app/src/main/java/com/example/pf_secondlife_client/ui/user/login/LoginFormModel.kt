package com.example.pf_secondlife_client.ui.user.login

sealed class LoginFormModel {
    object Idle: LoginFormModel()
    object Submitting: LoginFormModel()
    data class Error(val message: String): LoginFormModel()
}