package com.example.pf_secondlife_client.api.payments

import com.example.pf_secondlife_client.api.posts.PostsEndpoints

object PaymentsEndpoints {
    fun forPost(postId: String) = "${PostsEndpoints.BASE}/$postId/payments"
}