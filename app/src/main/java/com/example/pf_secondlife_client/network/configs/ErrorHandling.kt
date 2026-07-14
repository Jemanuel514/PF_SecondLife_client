package com.example.pf_secondlife_client.network.configs

import io.ktor.client.HttpClientConfig

fun HttpClientConfig<*>.configureErrorHandling() {
    expectSuccess = true
}