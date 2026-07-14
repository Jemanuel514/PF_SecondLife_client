package com.example.pf_secondlife_client.network.configs

import com.example.pf_secondlife_client.network.ApiConfig
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType

fun HttpClientConfig<*>.configureDefaultRequest() {
    defaultRequest {
        url(ApiConfig.BASE_URL)
        contentType(ContentType.Application.Json)
    }
}