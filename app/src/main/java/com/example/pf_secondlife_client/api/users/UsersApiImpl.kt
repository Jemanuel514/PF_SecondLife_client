package com.example.pf_secondlife_client.api.users

import com.example.pf_secondlife_client.api.errorHandling.safeApiCall
import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import com.example.pf_secondlife_client.domain.models.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class UsersApiImpl(private val client: HttpClient, private val tokenProvider: TokenProvider) : UsersApi {

    override suspend fun register(request: PostRegisterRequest): Result<PostRegisterResponse> {
        return safeApiCall(tokenProvider) {
            client.post(UsersEndpoints.REGISTER) {
                setBody(request)
            }.body()
        }
    }

    override suspend fun login(request: PostLoginRequest): Result<PostLoginResponse> {
        return safeApiCall(tokenProvider) {
            client.post(UsersEndpoints.LOGIN) {
                setBody(request)
            }.body()
        }
    }

    override suspend fun logout(): Result<Unit> {
        return safeApiCall(tokenProvider) { client.post(UsersEndpoints.LOGOUT).body() }
    }

    override suspend fun getUser(userId: String): Result<GetUserResponse> {
        return safeApiCall(tokenProvider) { client.get(UsersEndpoints.byId(userId)).body() }
    }

    override suspend fun getUserPosts(userId: String): Result<List<GetPostResponse>> {
        return safeApiCall(tokenProvider) { client.get(UsersEndpoints.postsByUserId(userId)).body() }
    }
}