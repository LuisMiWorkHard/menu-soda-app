package com.fullwar.menuapp.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object ImageCompressor {

    suspend fun compress(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 80
    ): ByteArray = withContext(Dispatchers.IO) {
        // Paso 1: Leer bounds sin cargar la imagen en memoria
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }

        val originalWidth = options.outWidth
        val originalHeight = options.outHeight

        // Paso 2: Calcular inSampleSize para reducción inicial eficiente
        options.inSampleSize = calculateInSampleSize(originalWidth, originalHeight, maxWidth, maxHeight)
        options.inJustDecodeBounds = false

        // Paso 3: Decode con inSampleSize
        val sampledBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        } ?: throw IllegalStateException("No se pudo leer la imagen")

        // Paso 4: Redimensionar si sigue siendo mayor al máximo
        val finalBitmap = if (sampledBitmap.width > maxWidth || sampledBitmap.height > maxHeight) {
            val scale = minOf(
                maxWidth.toFloat() / sampledBitmap.width,
                maxHeight.toFloat() / sampledBitmap.height
            )
            val newWidth = (sampledBitmap.width * scale).toInt()
            val newHeight = (sampledBitmap.height * scale).toInt()
            val scaled = Bitmap.createScaledBitmap(sampledBitmap, newWidth, newHeight, true)
            if (scaled != sampledBitmap) sampledBitmap.recycle()
            scaled
        } else {
            sampledBitmap
        }

        // Paso 5: Comprimir a JPEG
        val outputStream = ByteArrayOutputStream()
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        finalBitmap.recycle()

        outputStream.toByteArray()
    }

    private fun calculateInSampleSize(
        rawWidth: Int,
        rawHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Int {
        var inSampleSize = 1
        if (rawWidth > maxWidth || rawHeight > maxHeight) {
            val halfWidth = rawWidth / 2
            val halfHeight = rawHeight / 2
            while (halfWidth / inSampleSize >= maxWidth && halfHeight / inSampleSize >= maxHeight) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
