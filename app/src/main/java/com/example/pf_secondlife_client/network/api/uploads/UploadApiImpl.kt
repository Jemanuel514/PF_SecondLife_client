package com.example.pf_secondlife_client.network.api.uploads

import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import com.example.pf_secondlife_client.network.api.errorHandling.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UploadsApiImpl(private val client: HttpClient, private val tokenProvider: TokenProvider) : UploadsApi {
    override suspend fun uploadThumbnail(postId: String, fileName: String, thumbnail: ByteArray): Result<Unit> {
        return safeApiCall(tokenProvider) {
            client.post(UploadsEndpoints.thumbnail(postId)) {
                setBody(MultiPartFormDataContent(formData {
                    append(
                        key = "thumbnail",
                        value = thumbnail,
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        }
                    )
                }))
            }.body()
        }
    }
}