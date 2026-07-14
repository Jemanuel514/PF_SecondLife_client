package com.example.pf_secondlife_client.data.session.tokenProviders

import kotlinx.coroutines.flow.Flow

interface TokenProvider {
    fun observeUserId(): Flow<String?>
    suspend fun getAccessToken(): String?
    suspend fun saveSession(token: String, userId: String)
    suspend fun clearSession()
}