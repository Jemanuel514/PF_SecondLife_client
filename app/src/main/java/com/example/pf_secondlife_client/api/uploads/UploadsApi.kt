package com.example.pf_secondlife_client.api.uploads

interface UploadsApi  {
    suspend fun uploadThumbnail(postId: String, fileName: String, thumbnail: ByteArray): Result<Unit>
}