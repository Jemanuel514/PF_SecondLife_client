package com.example.pf_secondlife_client.domain.valuesSets

import kotlinx.serialization.Serializable

@Serializable
enum class PostState {
    AVAILABLE,
    BUSY,
    SOLD,
    HIDDEN,
    DELETED,
}