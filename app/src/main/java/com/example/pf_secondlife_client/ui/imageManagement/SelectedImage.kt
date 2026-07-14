package com.example.pf_secondlife_client.ui.imageManagement

import android.graphics.Bitmap
import android.net.Uri

sealed class SelectedImage {
    data class FromGallery(val uri: Uri) : SelectedImage()
    data class FromCamera(val bitmap: Bitmap) : SelectedImage()
}