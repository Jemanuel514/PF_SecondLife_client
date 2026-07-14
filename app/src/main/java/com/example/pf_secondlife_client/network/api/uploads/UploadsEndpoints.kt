package com.example.pf_secondlife_client.network.api.uploads

object UploadsEndpoints {
    fun thumbnail(postId: String) = "/uploads/thumbnails/$postId"
}