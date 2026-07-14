package com.example.pf_secondlife_client.api.users

import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.domain.models.GetUserResponse
import com.example.pf_secondlife_client.domain.models.PostLoginRequest
import com.example.pf_secondlife_client.domain.models.PostLoginResponse
import com.example.pf_secondlife_client.domain.models.PostRegisterRequest
import com.example.pf_secondlife_client.domain.models.PostRegisterResponse

interface UsersApi {
    suspend fun register(request: PostRegisterRequest): Result<PostRegisterResponse>
    suspend fun login(request: PostLoginRequest): Result<PostLoginResponse>
    suspend fun logout(): Result<Unit>
    suspend fun getUser(userId: String): Result<GetUserResponse>
    suspend fun getUserPosts(userId: String): Result<List<GetPostResponse>>
}