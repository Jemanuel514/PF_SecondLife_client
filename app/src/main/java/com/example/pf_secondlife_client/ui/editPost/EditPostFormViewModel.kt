package com.example.pf_secondlife_client.ui.editPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pf_secondlife_client.data.posts.PostsRepository
import com.example.pf_secondlife_client.data.uploads.UploadsRepository
import com.example.pf_secondlife_client.domain.models.PatchPostRequest
import com.example.pf_secondlife_client.domain.models.PutPostRequest
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.domain.valuesSets.PostState
import com.example.pf_secondlife_client.network.api.errorHandling.ApiException
import com.example.pf_secondlife_client.ui.imageManagement.SelectedImage
import com.example.pf_secondlife_client.ui.imageManagement.bytesReaders.ImageBytesReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class EditPostFormViewModel(
    private val postId: String,
    private val postsRepository: PostsRepository,
    private val uploadsRepository: UploadsRepository,
    private val imageBytesReader: ImageBytesReader,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPostFormModel>(EditPostFormModel.Loading)
    val uiState: StateFlow<EditPostFormModel> = _uiState.asStateFlow()

    private val _updateState = MutableStateFlow(EditPostModel())
    val updateState: StateFlow<EditPostModel> = _updateState.asStateFlow()

    private var lockAcquired = false

    private fun prepareEdit() {
        viewModelScope.launch {
            _uiState.value = EditPostFormModel.Loading

            val hide = postsRepository.patchPost(postId, PatchPostRequest(state = PostState.HIDDEN))
            if (hide.isFailure) {
                _uiState.value = EditPostFormModel.LockFailed
                return@launch
            }
            lockAcquired = true

            postsRepository.getPost(postId)
                .onSuccess { post ->
                    _updateState.value = EditPostModel(
                        title = post.title,
                        type = post.type,
                        brand = post.brand,
                        size = post.size,
                        color = post.color,
                        observations = post.observations ?: "",
                        price = post.price,
                        currentThumbnailPath = post.thumbnailPath,
                    )
                    _uiState.value = EditPostFormModel.Ready
                }
                .onFailure { error ->
                    _uiState.value = EditPostFormModel.Error(error.message ?: "Couldn't load post.")
                }
        }
    }

    fun onTitleChange(value: String) {
        _updateState.value = _updateState.value.copy(
            title = value,
            titleError = if (value.isBlank()) "Enter a title" else null,
        )
    }

    fun onTypeChange(value: ArticleType) {
        _updateState.value = _updateState.value.copy(type = value)
    }

    fun onBrandChange(value: String) {
        _updateState.value = _updateState.value.copy(
            brand = value,
            brandError = if (value.isBlank()) "Enter the brand" else null,
        )
    }

    fun onSizeChange(value: String) {
        _updateState.value = _updateState.value.copy(
            size = value,
            sizeError = if (value.isBlank()) "Enter the size" else null,
        )
    }

    fun onColorChange(value: String) {
        _updateState.value = _updateState.value.copy(
            color = value,
            colorError = if (value.isBlank()) "Enter the color" else null,
        )
    }

    fun onObservationsChange(value: String) {
        _updateState.value = _updateState.value.copy(observations = value)
    }

    fun onPriceChange(value: String) {
        val normalized = value.replace(',', '.')
        val error = when {
            normalized.isBlank() -> "Enter the price"
            normalized.toBigDecimalOrNull() == null -> "Invalid price"
            normalized.toBigDecimal() <= BigDecimal.ZERO -> "Price must be greater than zero."
            else -> null
        }
        _updateState.value = _updateState.value.copy(price = normalized, priceError = error)
    }

    fun onImageSelected(image: SelectedImage) {
        _updateState.value = _updateState.value.copy(newImage = image)
    }

    fun submit() {
        val form = _updateState.value
        if (!form.isValid()) return

        viewModelScope.launch {
            _uiState.value = EditPostFormModel.Submitting

            val updateResult = postsRepository.updatePost(
                postId,
                PutPostRequest(
                    title = form.title,
                    type = form.type!!,
                    brand = form.brand,
                    size = form.size,
                    color = form.color,
                    price = form.price,
                    observations = form.observations.ifBlank { null },
                )
            )

            if (updateResult.isFailure) {
                val error = updateResult.exceptionOrNull()
                _uiState.value = EditPostFormModel.Error(
                    (error as? ApiException)?.message ?: "Couldn't update post."
                )
                return@launch
            }

            form.newImage?.let { image ->
                val bytes = withContext(Dispatchers.IO) { imageBytesReader.readBytes(image) }
                val fileName = imageBytesReader.fileName(image)
                val uploadResult = uploadsRepository.uploadThumbnail(postId, fileName, bytes)
                if (uploadResult.isFailure) {
                    _uiState.value = EditPostFormModel.Error("Couldn't upload thumbnail.")
                    return@launch
                }
            }

            postsRepository.patchPost(postId, PatchPostRequest(state = PostState.AVAILABLE))
            lockAcquired = false
            _uiState.value = EditPostFormModel.Success
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (lockAcquired) {
            CoroutineScope(SupervisorJob()).launch {
                withContext(NonCancellable) {
                    postsRepository.patchPost(postId, PatchPostRequest(state = PostState.AVAILABLE))
                }
            }
        }
    }

    init {
        prepareEdit()
    }
}