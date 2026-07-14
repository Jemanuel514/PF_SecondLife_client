package com.example.pf_secondlife_client.domain.models

import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.domain.valuesSets.PostState
import kotlinx.serialization.Serializable

@Serializable
data class PostPostRequest(
    val title: String,
    val type: ArticleType,
    val brand: String,
    val size: String,
    val color: String,
    val observations: String? = null,
    val price: String,
)

@Serializable
data class PostPostResponse(
    val postId: String,
)

@Serializable
data class GetPostResponse(
    val id: String,
    val title: String,
    val type: ArticleType,
    val brand: String,
    val size: String,
    val color: String,
    val observations: String? = null,
    val price: String,
    val thumbnailPath: String? = null,
    val state: PostState,
    val timestamp: Long,
    val author: GetUserResponse,
    val payment: GetPaymentResponse? = null,
)

@Serializable
data class PutPostRequest(
    val title: String,
    val type: ArticleType,
    val brand: String,
    val size: String,
    val color: String,
    val price: String,
    val observations: String? = null,
)

@Serializable
data class PatchPostRequest(
    val state: PostState? = null,
)