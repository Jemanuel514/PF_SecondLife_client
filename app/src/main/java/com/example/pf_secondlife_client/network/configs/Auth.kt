package com.example.pf_secondlife_client.network.configs

import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer

fun HttpClientConfig<*>.configureAuth(tokenProvider: TokenProvider) {
    install(Auth) {
        bearer {
            loadTokens {
                tokenProvider.getAccessToken()?.let { BearerTokens(it, "") }
            }
            refreshTokens {
                null
            }
            sendWithoutRequest { true }
        }
    }
}