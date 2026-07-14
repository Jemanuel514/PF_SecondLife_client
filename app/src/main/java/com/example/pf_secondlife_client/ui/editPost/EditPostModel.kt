package com.example.pf_secondlife_client.ui.editPost

import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.ui.imageManagement.SelectedImage

data class EditPostModel(
    val title: String = "",
    val type: ArticleType? = null,
    val brand: String = "",
    val size: String = "",
    val color: String = "",
    val observations: String = "",
    val price: String = "",
    val currentThumbnailPath: String? = null,
    val newImage: SelectedImage? = null,

    val titleError: String? = null,
    val brandError: String? = null,
    val sizeError: String? = null,
    val colorError: String? = null,
    val priceError: String? = null,
) {
    fun isValid(): Boolean {
        return type != null &&
               title.isNotBlank() && titleError == null &&
               brand.isNotBlank() && brandError == null &&
               size.isNotBlank() &&  sizeError == null &&
               color.isNotBlank() && colorError == null &&
               price.isNotBlank() && priceError == null
    }
}