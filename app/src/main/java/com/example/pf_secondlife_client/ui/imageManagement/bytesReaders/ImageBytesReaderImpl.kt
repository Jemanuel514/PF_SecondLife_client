package com.example.pf_secondlife_client.ui.imageManagement.bytesReaders

import android.content.Context
import android.graphics.Bitmap
import android.provider.OpenableColumns
import com.example.pf_secondlife_client.ui.imageManagement.SelectedImage
import java.io.ByteArrayOutputStream

class ImageBytesReaderImpl(private val context: Context) : ImageBytesReader {

    override fun readBytes(image: SelectedImage): ByteArray {
        return when (image) {
            is SelectedImage.FromGallery ->
                context.contentResolver.openInputStream(image.uri)?.use {
                    it.readBytes()
                } ?: throw IllegalStateException("Couldn't read image")

            is SelectedImage.FromCamera ->
                ByteArrayOutputStream().use { stream ->
                    image.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.toByteArray()
                }
        }
    }

    override fun fileName(image: SelectedImage): String {
        return when (image) {
            is SelectedImage.FromGallery -> {
                context.contentResolver.query(
                    image.uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getString(0) else null
                } ?: "thumbnail_${System.currentTimeMillis()}.jpg"
            }

            is SelectedImage.FromCamera -> "thumbnail_${System.currentTimeMillis()}.jpg"
        }
    }
}