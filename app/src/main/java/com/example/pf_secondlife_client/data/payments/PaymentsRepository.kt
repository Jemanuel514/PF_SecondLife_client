package com.example.pf_secondlife_client.data.payments

import com.example.pf_secondlife_client.domain.models.PostPaymentRequest

interface PaymentsRepository {
    suspend fun createPayment(postId: String, request: PostPaymentRequest): Result<Unit>
}