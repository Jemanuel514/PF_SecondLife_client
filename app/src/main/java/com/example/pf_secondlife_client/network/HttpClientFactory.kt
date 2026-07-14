package com.example.pf_secondlife_client.network

import com.example.pf_secondlife_client.network.configs.configureAuth
import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import com.example.pf_secondlife_client.network.configs.configureDefaultRequest
import com.example.pf_secondlife_client.network.configs.configureErrorHandling
import com.example.pf_secondlife_client.network.configs.configureLogging
import com.example.pf_secondlife_client.network.configs.configureSerialization
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

object HttpClientFactory {
    fun create(tokenProvider: TokenProvider): HttpClient {
        return HttpClient(Android) {
            configureSerialization()
            configureLogging()
            configureDefaultRequest()
            configureErrorHandling()
            configureAuth(tokenProvider)
        }
    }
}