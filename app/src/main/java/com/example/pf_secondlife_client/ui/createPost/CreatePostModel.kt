package com.example.pf_secondlife_client.ui.createPost

import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.ui.imageManagement.SelectedImage

data class CreatePostModel(
    val title: String = "",
    val type: ArticleType? = null,
    val brand: String = "",
    val size: String = "",
    val color: String = "",
    val observations: String = "",
    val price: String = "",
    val image: SelectedImage? = null,

    val titleError: String? = null,
    val brandError: String? = null,
    val sizeError: String? = null,
    val colorError: String? = null,
    val priceError: String? = null,
    val imageError: String? = null,
) {
    fun isValid(): Boolean {
        return type != null &&
               title.isNotBlank() && titleError == null &&
               brand.isNotBlank() && brandError == null &&
               size.isNotBlank() &&  sizeError == null &&
               color.isNotBlank() && colorError == null &&
               price.isNotBlank() && priceError == null &&
               image != null
    }
}