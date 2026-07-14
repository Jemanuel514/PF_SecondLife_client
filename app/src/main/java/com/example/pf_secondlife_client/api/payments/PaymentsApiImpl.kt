package com.example.pf_secondlife_client.api.payments

import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import com.example.pf_secondlife_client.domain.models.PostPaymentRequest
import com.example.pf_secondlife_client.api.errorHandling.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class PaymentsApiImpl(private val client: HttpClient, private val tokenProvider: TokenProvider) :
    com.example.pf_secondlife_client.api.payments.PaymentsApi {

    override suspend fun createPayment(postId: String, request: PostPaymentRequest): Result<Unit> {
        return safeApiCall(tokenProvider) {
            client.post(PaymentsEndpoints.forPost(postId)) {
                setBody(request)
            }.body()
        }
    }
}