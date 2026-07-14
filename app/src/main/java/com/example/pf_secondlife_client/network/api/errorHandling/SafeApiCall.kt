package com.example.pf_secondlife_client.network.api.errorHandling

import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import java.io.IOException

suspend fun <T> safeApiCall(tokenProvider: TokenProvider, block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: ClientRequestException) {
        val body = e.response.bodyAsText()
        val exception = when (e.response.status) {
            HttpStatusCode.BadRequest -> ApiException.BadRequest(body)
            HttpStatusCode.Unauthorized -> {
                tokenProvider.clearSession()
                ApiException.Unauthorized(body)
            }
            HttpStatusCode.Forbidden -> ApiException.Forbidden(body)
            HttpStatusCode.NotFound -> ApiException.NotFound(body)
            HttpStatusCode.Conflict -> ApiException.Conflict(body)
            else -> ApiException.Unknown(e.response.status.value, body)
        }
        Result.failure(exception)
    } catch (e: ServerResponseException) {
        Result.failure(ApiException.ServerError(e.response.status.value))
    } catch (e: IOException) {
        Result.failure(ApiException.Network(e))
    }
}