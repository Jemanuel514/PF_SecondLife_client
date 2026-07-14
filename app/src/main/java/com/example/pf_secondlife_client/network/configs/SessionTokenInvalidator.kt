package com.example.pf_secondlife_client.network.configs

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.pluginOrNull

fun HttpClient.invalidateBearerTokenCache() {
    pluginOrNull(Auth)
        ?.providers
        ?.filterIsInstance<BearerAuthProvider>()
        ?.firstOrNull()
        ?.clearToken()
}