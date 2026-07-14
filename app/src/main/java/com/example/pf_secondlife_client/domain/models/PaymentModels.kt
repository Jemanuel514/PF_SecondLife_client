package com.example.pf_secondlife_client.domain.models

import com.example.pf_secondlife_client.domain.valuesSets.CardType
import kotlinx.serialization.Serializable

@Serializable
data class PostPaymentRequest(
    val cardType: CardType,
    val owner: String,
    val cardNumber: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    val cvv: String
)

@Serializable
data class GetPaymentResponse(
    val buyerId: String,
    val postId: String,
    val cardType: CardType,
    val timestamp: Long,
)