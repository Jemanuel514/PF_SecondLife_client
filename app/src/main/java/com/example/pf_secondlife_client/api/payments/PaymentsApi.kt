package com.example.pf_secondlife_client.api.payments

import com.example.pf_secondlife_client.domain.models.PostPaymentRequest

interface PaymentsApi {
    suspend fun createPayment(postId: String, request: PostPaymentRequest): Result<Unit>
}