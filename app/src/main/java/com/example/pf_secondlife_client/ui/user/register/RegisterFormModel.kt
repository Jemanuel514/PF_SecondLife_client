package com.example.pf_secondlife_client.ui.user.register

sealed class RegisterFormModel {
    object Idle : RegisterFormModel()
    object Submitting : RegisterFormModel()
    data class Error(val message: String) : RegisterFormModel()
}