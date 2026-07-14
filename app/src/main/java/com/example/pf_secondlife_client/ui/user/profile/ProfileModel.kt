package com.example.pf_secondlife_client.ui.user.profile

import com.example.pf_secondlife_client.domain.models.GetPostResponse
import com.example.pf_secondlife_client.domain.models.GetUserResponse
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType

sealed class ProfileModel {
    object Loading : ProfileModel()
    data class Success(
        val user: GetUserResponse,
        val posts: List<GetPostResponse>,
        val statsFilter: ArticleType? = null,
    ) : ProfileModel() {
        fun getStats(): ProfileStats {
            return computeProfileStats(posts, statsFilter)
        }
    }
    data class Error(val message: String) : ProfileModel()
}