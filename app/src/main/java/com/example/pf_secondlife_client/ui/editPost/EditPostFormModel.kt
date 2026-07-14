package com.example.pf_secondlife_client.ui.editPost

sealed class EditPostFormModel {
    object Loading : EditPostFormModel()
    object LockFailed : EditPostFormModel()
    object Ready : EditPostFormModel()
    object Submitting : EditPostFormModel()
    object Success : EditPostFormModel()
    data class Error(val message: String) : EditPostFormModel()
}