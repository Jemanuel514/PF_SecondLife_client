package com.example.pf_secondlife_client.data.session.sessionManagers

import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val userId: StateFlow<String?>
    suspend fun getCurrentUserId(): String? {
        return userId.value
    }
}