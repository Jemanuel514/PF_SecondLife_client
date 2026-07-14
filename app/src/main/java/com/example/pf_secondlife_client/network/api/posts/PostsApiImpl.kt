package com.example.pf_secondlife_client.network.api.posts

import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import com.example.pf_secondlife_client.domain.models.*
import com.example.pf_secondlife_client.network.api.errorHandling.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class PostsApiImpl(private val client: HttpClient, private val tokenProvider: TokenProvider) : PostsApi {

    override suspend fun getPosts(): Result<List<GetPostResponse>> {
        return safeApiCall(tokenProvider) { client.get(PostsEndpoints.BASE).body() }
    }

    override suspend fun getPost(postId: String): Result<GetPostResponse> {
        return safeApiCall(tokenProvider) { client.get(PostsEndpoints.byId(postId)).body() }
    }

    override suspend fun createPost(request: PostPostRequest): Result<PostPostResponse> {
        return safeApiCall(tokenProvider) {
            client.post(PostsEndpoints.BASE) {
                setBody(request)
            }.body()
        }
    }

    override suspend fun updatePost(postId: String, request: PutPostRequest): Result<Unit> {
        return safeApiCall(tokenProvider) {
            client.put(PostsEndpoints.byId(postId)) {
                setBody(request)
            }.body()
        }
    }

    override suspend fun patchPost(postId: String, request: PatchPostRequest): Result<Unit> {
        return safeApiCall(tokenProvider) {
            client.patch(PostsEndpoints.byId(postId)) {
                setBody(request)
            }.body()
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return safeApiCall(tokenProvider) {
            client.delete(PostsEndpoints.byId(postId)).body()
        }
    }
}