package com.example.pf_secondlife_client.ui.createPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pf_secondlife_client.data.posts.PostsRepository
import com.example.pf_secondlife_client.data.uploads.UploadsRepository
import com.example.pf_secondlife_client.domain.models.PostPostRequest
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.ui.imageManagement.SelectedImage
import com.example.pf_secondlife_client.ui.imageManagement.bytesReaders.ImageBytesReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class CreatePostFormViewModel(
    private val postsRepository: PostsRepository,
    private val uploadsRepository: UploadsRepository,
    private val imageBytesReader: ImageBytesReader,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreatePostFormModel>(CreatePostFormModel.Idle)
    val uiState: StateFlow<CreatePostFormModel> = _uiState.asStateFlow()

    private val _createState = MutableStateFlow(CreatePostModel())
    val createState: StateFlow<CreatePostModel> = _createState.asStateFlow()

    fun onTitleChange(value: String) {
        _createState.value = _createState.value.copy(
            title = value,
            titleError = if (value.isBlank()) "Enter a title" else null,
        )
    }

    fun onTypeChange(value: ArticleType) {
        _createState.value = _createState.value.copy(type = value)
    }

    fun onBrandChange(value: String) {
        _createState.value = _createState.value.copy(
            brand = value,
            brandError = if (value.isBlank()) "Enter the brand" else null,
        )
    }

    fun onSizeChange(value: String) {
        _createState.value = _createState.value.copy(
            size = value,
            sizeError = if (value.isBlank()) "Enter the size" else null,
        )
    }

    fun onColorChange(value: String) {
        _createState.value = _createState.value.copy(
            color = value,
            colorError = if (value.isBlank()) "Enter the color" else null,
        )
    }

    fun onObservationsChange(value: String) {
        _createState.value = _createState.value.copy(observations = value)
    }

    fun onPriceChange(value: String) {
        val normalized = value.replace(',', '.')
        val error = when {
            normalized.isBlank() -> "Enter the price"
            normalized.toBigDecimalOrNull() == null -> "Invalid price"
            normalized.toBigDecimal() <= BigDecimal.ZERO -> "Price must be greater than zero."
            else -> null
        }
        _createState.value = _createState.value.copy(price = normalized, priceError = error)
    }

    fun onImageSelected(image: SelectedImage) {
        _createState.value = _createState.value.copy(image = image, imageError = null)
    }

    fun submit() {
        val form = _createState.value
        if (!form.isValid()) return

        viewModelScope.launch {
            _uiState.value = CreatePostFormModel.Submitting

            val create = postsRepository.createPost(
                PostPostRequest(
                    title = form.title,
                    type = form.type!!,
                    brand = form.brand,
                    size = form.size,
                    color = form.color,
                    price = form.price,
                    observations = form.observations.ifBlank { null },
                )
            )

            val postId = create.getOrNull()?.postId
            if (postId == null) {
                _uiState.value = CreatePostFormModel.Error(
                    create.exceptionOrNull()?.message ?: "Couldn't create post."
                )
                return@launch
            }

            val image = form.image!!
            val bytes = withContext(Dispatchers.IO) { imageBytesReader.readBytes(image) }
            val fileName = imageBytesReader.fileName(image)

            uploadsRepository.uploadThumbnail(postId, fileName, bytes)
                .onSuccess { _uiState.value = CreatePostFormModel.Success(postId) }
                .onFailure {
                    _uiState.value = CreatePostFormModel.Success(postId)
                }
        }
    }
}