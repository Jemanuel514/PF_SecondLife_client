package com.example.pf_secondlife_client.data.posts

import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.domain.models.PatchPostRequest
import com.example.pf_secondlife_client.domain.models.PostPostRequest
import com.example.pf_secondlife_client.domain.models.PostPostResponse
import com.example.pf_secondlife_client.domain.models.PutPostRequest

interface PostsRepository {
    suspend fun getPosts(): Result<List<GetPostResponse>>
    suspend fun getPost(postId: String): Result<GetPostResponse>
    suspend fun createPost(request: PostPostRequest): Result<PostPostResponse>
    suspend fun updatePost(postId: String, request: PutPostRequest): Result<Unit>
    suspend fun patchPost(postId: String, request: PatchPostRequest): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit>
}