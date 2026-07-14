package com.example.pf_secondlife_client.data.posts

import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.domain.models.PatchPostRequest
import com.example.pf_secondlife_client.domain.models.PostPostRequest
import com.example.pf_secondlife_client.domain.models.PostPostResponse
import com.example.pf_secondlife_client.domain.models.PutPostRequest
import com.example.pf_secondlife_client.network.api.posts.PostsApi

class PostsRepositoryImpl(private val api: PostsApi) : PostsRepository {

    override suspend fun getPosts(): Result<List<GetPostResponse>> {
        return api.getPosts()
    }

    override suspend fun getPost(postId: String): Result<GetPostResponse> {
        return api.getPost(postId)
    }

    override suspend fun createPost(request: PostPostRequest): Result<PostPostResponse> {
        return api.createPost(request)
    }

    override suspend fun updatePost(postId: String, request: PutPostRequest): Result<Unit> {
        return api.updatePost(postId, request)
    }

    override suspend fun patchPost(postId: String, request: PatchPostRequest): Result<Unit> {
        return api.patchPost(postId, request)
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return api.deletePost(postId)
    }
}