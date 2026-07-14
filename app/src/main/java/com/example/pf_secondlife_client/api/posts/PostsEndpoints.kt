package com.example.pf_secondlife_client.api.posts

object PostsEndpoints {
    const val BASE = "/posts"

    fun byId(postId: String) = "$BASE/$postId"
}