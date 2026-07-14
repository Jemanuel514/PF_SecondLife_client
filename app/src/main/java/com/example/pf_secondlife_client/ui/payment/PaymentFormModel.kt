package com.example.pf_secondlife_client.ui.payment

sealed class PaymentFormModel {
    object Locking : PaymentFormModel()
    object LockFailed : PaymentFormModel()
    object Ready : PaymentFormModel()
    object Submitting : PaymentFormModel()
    object Success: PaymentFormModel()
    data class Error(val message: String) : PaymentFormModel()
}