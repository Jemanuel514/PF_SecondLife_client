package com.example.pf_secondlife_client.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pf_secondlife_client.network.ImageUrlBuilder
import com.example.pf_secondlife_client.ui.imageManagement.SelectedImage

@Composable
fun ThumbnailPicker(
    currentThumbnailPath: String?,
    newImage: SelectedImage?,
    onImageSelected: (SelectedImage) -> Unit,
) {
    val context = LocalContext.current
    var showSourceDialog by remember { mutableStateOf(false) }
    var pendingCameraLaunch by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { onImageSelected(SelectedImage.FromGallery(it)) } }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> bitmap?.let { onImageSelected(SelectedImage.FromCamera(it)) } }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) cameraLauncher.launch() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            newImage is SelectedImage.FromCamera -> {
                Image(
                    bitmap = newImage.bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            newImage is SelectedImage.FromGallery -> {
                AsyncImage(
                    model = newImage.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            currentThumbnailPath != null -> {
                AsyncImage(
                    model = ImageUrlBuilder.build(currentThumbnailPath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            else -> {
                Text(text = "No image")
            }
        }

        FilledTonalButton(
            onClick = { showSourceDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
        ) {
            Text(text = "Change image")
        }
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text(text = "Select image") },
            text = { Text(text = "Select from") },
            confirmButton = {
                TextButton(onClick = {
                    showSourceDialog = false
                    galleryLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) { Text(text = "Gallery") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSourceDialog = false
                    val hasPermission = context.checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        cameraLauncher.launch()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) { Text(text = "Camera") }
            }
        )
    }
}