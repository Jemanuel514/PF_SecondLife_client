package com.example.pf_secondlife_client.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class PostRegisterRequest(
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
data class PostRegisterResponse(
    val id: String,
    val active: Boolean,
)

@Serializable
data class PostLoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class PostLoginResponse(
    val id: String,
    val token: String,
)

@Serializable
data class GetUserResponse(
    val id: String,
    val username: String,
    val email: String,
    val active: Boolean,
)