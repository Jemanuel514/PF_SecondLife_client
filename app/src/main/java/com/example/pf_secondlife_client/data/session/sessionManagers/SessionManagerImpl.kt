package com.example.pf_secondlife_client.data.session.sessionManagers

import com.example.pf_secondlife_client.data.session.tokenProviders.TokenProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SessionManagerImpl(tokenProvider: TokenProvider, scope: CoroutineScope) : SessionManager {
    override val userId: StateFlow<String?> = tokenProvider.observeUserId().stateIn(scope, SharingStarted.Eagerly, null)
}