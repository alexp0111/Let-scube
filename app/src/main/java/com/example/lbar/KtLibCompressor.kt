package com.example.lbar

import android.net.Uri
import androidx.core.net.toFile
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.io.File

class KtLibCompressor(val file: File) {

    fun getCompressedImage(context: android.content.Context): Uri {
        // Короче разбирайся с корутинами

        var newUri: Uri = Uri.EMPTY
        runBlocking {
            val compressedImageFile: File = Compressor.compress(context, file, Dispatchers.Main)

            newUri = Uri.fromFile(compressedImageFile)
        }
        return newUri
    }
}