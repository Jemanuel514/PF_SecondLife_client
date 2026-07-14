package com.example.pf_secondlife_client.ui.postDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pf_secondlife_client.data.posts.PostsRepository
import com.example.pf_secondlife_client.data.session.sessionManagers.SessionManager
import com.example.pf_secondlife_client.domain.valuesSets.PostState
import com.example.pf_secondlife_client.network.api.errorHandling.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val postId: String,
    private val postsRepository: PostsRepository,
    private val sessionManager: SessionManager
) : ViewModel(){
    private val _uiState = MutableStateFlow<PostDetailModel>(PostDetailModel.Loading)
    val uiState: StateFlow<PostDetailModel> = _uiState.asStateFlow()

    private val _purchaseCheckResult = MutableStateFlow<PurchaseCheckResult?>(null)
    val purchaseCheckResult: StateFlow<PurchaseCheckResult?> = _purchaseCheckResult.asStateFlow()

    fun loadPost() {
        viewModelScope.launch {
            _uiState.value = PostDetailModel.Loading
            postsRepository.getPost(postId)
                .onSuccess { post ->
                    _uiState.value = PostDetailModel.Success(post)
                }
                .onFailure { error ->
                    val message = when (error) {
                        is ApiException.NotFound -> "This post is not available."
                        is ApiException.Network -> "Not connected. Check your internet connection."
                        else -> error.message ?: "Unknown error."
                    }
                    _uiState.value = PostDetailModel.Error(message)
                }
        }
    }

    fun onBuyClicked() {
        viewModelScope.launch {
            val currentUserId = sessionManager.getCurrentUserId()
            if (currentUserId == null) {
                _purchaseCheckResult.value = PurchaseCheckResult.RequiresLogin
                return@launch
            }

            postsRepository.getPost(postId)
                .onSuccess { post ->
                    _purchaseCheckResult.value = when {
                        post.author.id == currentUserId -> PurchaseCheckResult.IsOwner
                        post.state != PostState.AVAILABLE -> PurchaseCheckResult.NotAvailable
                        else -> PurchaseCheckResult.Allowed(post.id)
                    }
                }
                .onFailure { error ->
                    _purchaseCheckResult.value = PurchaseCheckResult.Error(
                        error.message ?: "Couldn't check the post. Please try again."
                    )
                }
        }
    }

    fun consumePurchaseCheckResult() {
        _purchaseCheckResult.value = null
    }

    init {
        loadPost()
    }
}