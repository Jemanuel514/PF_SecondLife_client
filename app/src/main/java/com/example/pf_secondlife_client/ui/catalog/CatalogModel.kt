package com.example.pf_secondlife_client.ui.catalog

import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType

sealed class CatalogModel {
    object Loading: CatalogModel()
    data class Success(val allPosts: List<GetPostResponse>, val selectedFilter: ArticleType? = null, ): CatalogModel() {
        val filteredPosts: List<GetPostResponse> get() =
            if (selectedFilter == null) allPosts
            else allPosts.filter { it.type == selectedFilter }
    }
    data class Error(val message: String): CatalogModel()
}