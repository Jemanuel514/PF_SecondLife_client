package com.example.pf_secondlife_client.ui.createPost

sealed class CreatePostFormModel {
    object Idle : CreatePostFormModel()
    object Submitting : CreatePostFormModel()
    data class Success(val postId: String) : CreatePostFormModel()
    data class Error(val message: String) : CreatePostFormModel()
}