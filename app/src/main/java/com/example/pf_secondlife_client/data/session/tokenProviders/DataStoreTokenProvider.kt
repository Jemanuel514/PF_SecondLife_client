package com.example.pf_secondlife_client.data.session.tokenProviders

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth")

class DataStoreTokenProvider(private val context: Context) : TokenProvider {
    private val tokenKey = stringPreferencesKey("access_token")
    private val userIdKey = stringPreferencesKey("user_id")

    override fun observeUserId(): Flow<String?> {
        return context.dataStore.data.map { it[userIdKey] }
    }

    override suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { it[tokenKey] }.firstOrNull()
    }

    override suspend fun saveSession(token: String, userId: String) {
        context.dataStore.edit {
            it[tokenKey] = token
            it[userIdKey] = userId
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(tokenKey)
            it.remove(userIdKey)
        }
    }
}