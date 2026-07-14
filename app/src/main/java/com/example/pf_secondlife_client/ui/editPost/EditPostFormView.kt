package com.example.pf_secondlife_client.ui.editPost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pf_secondlife_client.domain.valuesSets.ArticleType
import com.example.pf_secondlife_client.ui.components.ArticleTypeDropdown
import com.example.pf_secondlife_client.ui.components.ThumbnailPicker
import com.example.pf_secondlife_client.ui.imageManagement.SelectedImage
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
private fun EditPostForm(
    updateState: EditPostModel,
    isSubmitting: Boolean,
    onTitleChange: (String) -> Unit,
    onTypeChange: (ArticleType) -> Unit,
    onBrandChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onObservationsChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onImageSelected: (SelectedImage) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ThumbnailPicker(
            currentThumbnailPath = updateState.currentThumbnailPath,
            newImage = updateState.newImage,
            onImageSelected = onImageSelected,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = updateState.title,
            onValueChange = onTitleChange,
            label = { Text(text = "Title") },
            isError = updateState.titleError != null,
            supportingText = { updateState.titleError?.let { Text(text = it) } },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        ArticleTypeDropdown(selected = updateState.type, onSelected = onTypeChange)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = updateState.brand,
            onValueChange = onBrandChange,
            label = { Text(text = "Brand") },
            isError = updateState.brandError != null,
            supportingText = { updateState.brandError?.let { Text(text = it) } },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = updateState.size,
                onValueChange = onSizeChange,
                label = { Text(text = "Size") },
                isError = updateState.sizeError != null,
                supportingText = { updateState.sizeError?.let { Text(text = it) } },
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = updateState.color,
                onValueChange = onColorChange,
                label = { Text(text = "Color") },
                isError = updateState.colorError != null,
                supportingText = { updateState.colorError?.let { Text(text = it) } },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = updateState.observations,
            onValueChange = onObservationsChange,
            label = { Text(text = "Observations (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = updateState.price,
            onValueChange = onPriceChange,
            label = { Text(text = "Price") },
            isError = updateState.priceError != null,
            supportingText = { updateState.priceError?.let { Text(text = it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            enabled = updateState.isValid() && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(text = "Save changes")
            }
        }
    }
}

@Composable
fun EditPostFormView(
    postId: String,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: EditPostFormViewModel = koinViewModel(parameters = { parametersOf(postId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    when (val state = uiState) {
        is EditPostFormModel.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is EditPostFormModel.LockFailed -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Couldn't start update process.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCancel) { Text(text = "Go back") }
            }
        }

        is EditPostFormModel.Success -> {
            AlertDialog(
                onDismissRequest = onSaved,
                confirmButton = {
                    TextButton(onClick = onSaved) { Text("Accept") }
                },
                title = { Text("Changes saved") },
                text = { Text("Changes were saved successfully.") },
            )
        }

        is EditPostFormModel.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = state.message)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCancel) { Text(text = "Go back") }
            }
        }

        is EditPostFormModel.Ready, is EditPostFormModel.Submitting -> {
            EditPostForm(
                updateState = updateState,
                isSubmitting = uiState is EditPostFormModel.Submitting,
                onTitleChange = viewModel::onTitleChange,
                onTypeChange = viewModel::onTypeChange,
                onBrandChange = viewModel::onBrandChange,
                onSizeChange = viewModel::onSizeChange,
                onColorChange = viewModel::onColorChange,
                onObservationsChange = viewModel::onObservationsChange,
                onPriceChange = viewModel::onPriceChange,
                onImageSelected = viewModel::onImageSelected,
                onSubmit = viewModel::submit,
            )
        }
    }
}

