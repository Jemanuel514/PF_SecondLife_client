package com.example.pf_secondlife_client.data.payments

import com.example.pf_secondlife_client.domain.models.PostPaymentRequest
import com.example.pf_secondlife_client.network.api.payments.PaymentsApi

class PaymentsRepositoryImpl(private val api: PaymentsApi) : PaymentsRepository {

    override suspend fun createPayment(postId: String, request: PostPaymentRequest): Result<Unit> {
        return api.createPayment(postId, request)
    }
}