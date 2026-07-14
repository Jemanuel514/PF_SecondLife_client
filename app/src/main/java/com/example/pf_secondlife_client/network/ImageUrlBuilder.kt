package com.example.pf_secondlife_client.network

import com.example.pf_secondlife_client.network.httpClient.ApiConfig

object ImageUrlBuilder {
    fun build(relativePath: String?): String? {
        if (relativePath == null) return null
        return "${ApiConfig.BASE_URL}/$relativePath"
    }
}