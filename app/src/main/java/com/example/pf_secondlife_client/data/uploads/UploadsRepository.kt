package com.example.pf_secondlife_client.data.uploads

interface UploadsRepository {
    suspend fun uploadThumbnail(
        postId: String,
        fileName: String,
        thumbnail: ByteArray
    ): Result<Unit>
}