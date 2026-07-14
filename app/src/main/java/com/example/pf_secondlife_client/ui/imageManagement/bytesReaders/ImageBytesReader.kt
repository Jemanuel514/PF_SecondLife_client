package com.example.pf_secondlife_client.ui.imageManagement.bytesReaders

import com.example.pf_secondlife_client.ui.imageManagement.SelectedImage

interface ImageBytesReader {
    fun readBytes(image: SelectedImage): ByteArray
    fun fileName(image: SelectedImage): String
}