package com.example.pf_secondlife_client.ui.createPost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pf_secondlife_client.ui.components.ArticleTypeDropdown
import com.example.pf_secondlife_client.ui.components.ThumbnailPicker
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreatePostFormView(
    onCreated: () -> Unit,
    onCancel: () -> Unit,
    viewModel: CreatePostFormViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.createState.collectAsState()

    if (uiState is CreatePostFormModel.Success) {
        AlertDialog(
            onDismissRequest = onCreated,
            confirmButton = {
                TextButton(onClick = onCreated) { Text("Accept") }
            },
            title = { Text("Post created") },
            text = { Text("Post was created successfully.") },
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "New post", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ThumbnailPicker(
            currentThumbnailPath = null,
            newImage = formState.image,
            onImageSelected = viewModel::onImageSelected,
        )
        formState.imageError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = formState.title,
            onValueChange = viewModel::onTitleChange,
            label = { Text("Title") },
            isError = formState.titleError != null,
            supportingText = { formState.titleError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        ArticleTypeDropdown(selected = formState.type, onSelected = viewModel::onTypeChange)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.brand,
            onValueChange = viewModel::onBrandChange,
            label = { Text("Brand") },
            isError = formState.brandError != null,
            supportingText = { formState.brandError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.size,
                onValueChange = viewModel::onSizeChange,
                label = { Text("Size") },
                isError = formState.sizeError != null,
                supportingText = { formState.sizeError?.let { Text(it) } },
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = formState.color,
                onValueChange = viewModel::onColorChange,
                label = { Text("Color") },
                isError = formState.colorError != null,
                supportingText = { formState.colorError?.let { Text(it) } },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.observations,
            onValueChange = viewModel::onObservationsChange,
            label = { Text("Observations (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = formState.price,
            onValueChange = viewModel::onPriceChange,
            label = { Text("Price") },
            isError = formState.priceError != null,
            supportingText = { formState.priceError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiState is CreatePostFormModel.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as CreatePostFormModel.Error).message,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::submit,
            enabled = formState.isValid() && uiState !is CreatePostFormModel.Submitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState is CreatePostFormModel.Submitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Post")
            }
        }
    }
}