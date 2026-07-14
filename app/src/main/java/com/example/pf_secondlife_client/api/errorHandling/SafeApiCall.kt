package com.example.pf_secondlife_client.api.errorHandling

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
            HttpStatusCode.BadRequest -> _root_ide_package_.com.example.pf_secondlife_client.api.errorHandling.ApiException.BadRequest(body)
            HttpStatusCode.Unauthorized -> {
                tokenProvider.clearSession()
                _root_ide_package_.com.example.pf_secondlife_client.api.errorHandling.ApiException.Unauthorized(body)
            }
            HttpStatusCode.Forbidden -> _root_ide_package_.com.example.pf_secondlife_client.api.errorHandling.ApiException.Forbidden(body)
            HttpStatusCode.NotFound -> _root_ide_package_.com.example.pf_secondlife_client.api.errorHandling.ApiException.NotFound(body)
            HttpStatusCode.Conflict -> _root_ide_package_.com.example.pf_secondlife_client.api.errorHandling.ApiException.Conflict(body)
            else -> _root_ide_package_.com.example.pf_secondlife_client.api.errorHandling.ApiException.Unknown(e.response.status.value, body)
        }
        Result.failure(exception)
    } catch (e: ServerResponseException) {
        Result.failure(_root_ide_package_.com.example.pf_secondlife_client.api.errorHandling.ApiException.ServerError(e.response.status.value))
    } catch (e: IOException) {
        Result.failure(_root_ide_package_.com.example.pf_secondlife_client.api.errorHandling.ApiException.Network(e))
    }
}