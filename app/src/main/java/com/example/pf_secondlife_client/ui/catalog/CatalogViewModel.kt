package com.example.pf_secondlife_client.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pf_secondlife_client.data.posts.PostsRepository
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.network.api.errorHandling.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(private val postsRepository: PostsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<CatalogModel>(CatalogModel.Loading)
    val uiState: StateFlow<CatalogModel> = _uiState.asStateFlow()

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = CatalogModel.Loading
            postsRepository.getPosts()
                .onSuccess { posts ->
                    _uiState.value = CatalogModel.Success(posts)
                }
                .onFailure { error ->
                    val message = when (error) {
                        is ApiException.Network -> "Not connected. Check your internet connection."
                        else -> error.message ?: "Unknown error."
                    }
                    _uiState.value = CatalogModel.Error(message)
                }
        }
    }

    fun setFilter(type: ArticleType?) {
        val currentState = _uiState.value
        if (currentState is CatalogModel.Success) {
            _uiState.value = currentState.copy(selectedFilter = type)
        }
    }
}