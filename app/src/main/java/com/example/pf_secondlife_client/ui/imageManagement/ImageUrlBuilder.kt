package com.example.pf_secondlife_client.ui.imageManagement

import com.example.pf_secondlife_client.network.ApiConfig

object ImageUrlBuilder {
    fun build(relativePath: String?): String? {
        if (relativePath == null) return null
        return "${ApiConfig.BASE_URL}/$relativePath"
    }
}