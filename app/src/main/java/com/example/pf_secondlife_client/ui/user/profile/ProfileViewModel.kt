package com.example.pf_secondlife_client.ui.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pf_secondlife_client.data.posts.PostsRepository
import com.example.pf_secondlife_client.data.users.UsersRepository
import com.example.pf_secondlife_client.domain.models.PatchPostRequest
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.domain.valuesSets.PostState
import com.example.pf_secondlife_client.network.api.errorHandling.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userId: String,
    private val usersRepository: UsersRepository,
    private val postsRepository: PostsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileModel>(ProfileModel.Loading)
    val uiState: StateFlow<ProfileModel> = _uiState.asStateFlow()

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileModel.Loading
            val userResult = usersRepository.getUser(userId)
            val postsResult = usersRepository.getUserPosts(userId)

            val user = userResult.getOrNull()
            val posts = postsResult.getOrNull()

            if (user != null && posts != null) {
                _uiState.value = ProfileModel.Success(user = user, posts = posts)
            } else {
                val error = userResult.exceptionOrNull() ?: postsResult.exceptionOrNull()
                val message = when (error) {
                    is ApiException.Network -> "Not connected. Check your internet connection."
                    else -> error?.message ?: "Couln't load the profile."
                }
                _uiState.value = ProfileModel.Error(message)
            }
        }
    }

    fun setStatsFilter(type: ArticleType?) {
        val current = _uiState.value
        if (current is ProfileModel.Success) {
            _uiState.value = current.copy(statsFilter = type)
        }
    }

    fun toggleVisibility(postId: String, currentState: PostState) {
        val newState = if (currentState == PostState.HIDDEN) PostState.AVAILABLE else PostState.HIDDEN
        viewModelScope.launch {
            postsRepository.patchPost(postId, PatchPostRequest(state = newState))
                .onSuccess { loadProfile() }
                .onFailure { error -> _actionError.value = error.message ?: "Couldn't update the post." }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postsRepository.deletePost(postId)
                .onSuccess { loadProfile() }
                .onFailure { error -> _actionError.value = error.message ?: "Couldn't delete the post." }
        }
    }

    fun logout() {
        viewModelScope.launch {
            usersRepository.logout()
                .onFailure { error ->
                    _actionError.value = error.message ?: "Couldn't logout from the server."
                }
        }
    }

    fun consumeActionError() {
        _actionError.value = null
    }
}