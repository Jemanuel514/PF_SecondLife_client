package com.example.pf_secondlife_client.api.errorHandling

sealed class ApiException(message: String) : Exception(message) {
    data class BadRequest(val body: String) : ApiException(body)
    data class Unauthorized(val body: String) : ApiException(body)
    data class Forbidden(val body: String) : ApiException(body)
    data class NotFound(val body: String) : ApiException(body)
    data class Conflict(val body: String) : ApiException(body)
    data class ServerError(val statusCode: Int) : ApiException("Server error: $statusCode")
    data class Unknown(val statusCode: Int, val body: String) : ApiException(body)
    data class Network(override val cause: Throwable) : ApiException(cause.message ?: "Network error")
}