package com.example.pf_secondlife_client.ui.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pf_secondlife_client.data.session.sessionManagers.SessionManager
import com.example.pf_secondlife_client.ui.user.login.LoginFormView
import com.example.pf_secondlife_client.ui.user.profile.ProfileView
import com.example.pf_secondlife_client.ui.user.register.RegisterFormView
import org.koin.compose.koinInject

@Composable
private fun AuthFlow() {
    var showRegister by remember { mutableStateOf(false) }

    if (showRegister) {
        RegisterFormView(onNavigateToLogin = { showRegister = false })
    } else {
        LoginFormView(onNavigateToRegister = { showRegister = true })
    }
}

@Composable
fun UserTabRoot(
    onCreatePost: () -> Unit,
    onEditPost: (postId: String) -> Unit
) {
    val sessionManager: SessionManager = koinInject()
    val userId by sessionManager.userId.collectAsState()

    if (userId == null) {
        AuthFlow()
    } else {
        ProfileView(
            userId = userId!!,
            onCreatePost = onCreatePost,
            onEditPost = onEditPost
        )
    }
}