package com.example.pf_secondlife_client.data.users

import com.example.pf_secondlife_client.domain.models.GetUserResponse
import com.example.pf_secondlife_client.domain.models.PostLoginRequest
import com.example.pf_secondlife_client.domain.models.PostLoginResponse
import com.example.pf_secondlife_client.domain.models.PostRegisterRequest
import com.example.pf_secondlife_client.domain.models.PostRegisterResponse
import com.example.pf_secondlife_client.network.api.users.UsersApi
import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.network.httpClient.configs.invalidateBearerTokenCache
import io.ktor.client.HttpClient

class UsersRepositoryImpl(
    private val api: UsersApi,
    private val tokenProvider: TokenProvider,
    private val httpClient: HttpClient
): UsersRepository {

    override suspend fun register(request: PostRegisterRequest): Result<PostRegisterResponse> {
         return api.register(request)
    }

    override suspend fun login(request: PostLoginRequest): Result<PostLoginResponse> {
        val result = api.login(request)
        result.onSuccess { response ->
            tokenProvider.saveSession(response.token, response.id)
            httpClient.invalidateBearerTokenCache()
        }
        return result
    }

    override suspend fun logout(): Result<Unit> {
        val result = api.logout()
        tokenProvider.clearSession()
        httpClient.invalidateBearerTokenCache()
        return result
    }

    override suspend fun getUser(userId: String): Result<GetUserResponse> {
        return api.getUser(userId)
    }

    override suspend fun getUserPosts(userId: String): Result<List<GetPostResponse>> {
        return api.getUserPosts(userId)
    }
}