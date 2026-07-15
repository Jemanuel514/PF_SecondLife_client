package com.example.pf_secondlife_client.data.uploads

import com.example.pf_secondlife_client.api.uploads.UploadsApi

class UploadsRepositoryImpl(private val api: UploadsApi) : UploadsRepository {

    override suspend fun uploadThumbnail(postId: String, fileName: String, thumbnail: ByteArray): Result<Unit> {
        return api.uploadThumbnail(postId, fileName, thumbnail)
    }
}