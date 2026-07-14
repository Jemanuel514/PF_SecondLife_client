package com.example.pf_secondlife_client.ui.postDetail

import com.example.pf_secondlife_client.domain.models.GetPostResponse

sealed class PostDetailModel {
    object Loading : PostDetailModel()
    data class Success(val post: GetPostResponse) : PostDetailModel()
    data class Error(val message: String) : PostDetailModel()
}

sealed class PurchaseCheckResult {
    object RequiresLogin : PurchaseCheckResult()
    object NotAvailable : PurchaseCheckResult()
    object IsOwner : PurchaseCheckResult()
    data class Allowed(val postId: String) : PurchaseCheckResult()
    data class Error(val message: String) : PurchaseCheckResult()
}