package com.example.pf_secondlife_client.ui.payment

import com.example.pf_secondlife_client.domain.valuesSets.CardType

data class PaymentModel(
    val cardType: CardType? = null,
    val cardholderName: String = "",
    val cardNumber: String = "",
    val expiration: String = "",
    val cvv: String = "",

    val cardTypeError: String? = null,
    val cardholderNameError: String? = null,
    val cardNumberError: String? = null,
    val expirationError: String? = null,
    val cvvError: String? = null,
) {
    fun isValid(): Boolean {
        return cardType != null &&            cardTypeError == null &&
               cardholderName.isNotBlank() && cardholderNameError == null &&
               cardNumber.isNotBlank() &&     cardNumberError == null &&
               expiration.length == 4 &&      expirationError == null &&
               cvv.isNotBlank() &&            cvvError == null
    }
}