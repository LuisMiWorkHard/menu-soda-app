package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.ImagenUploadResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class ImagenService(private val httpClient: HttpClient) {

    companion object {
        private const val TAG = "ImagenService"
    }

    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        extension: String
    ): ImagenUploadResponseDto {
        val response = httpClient.submitFormWithBinaryData(
            url = "api/imagen/upload",
            formData = formData {
                append("file", imageBytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName$extension\"")
                    append(HttpHeaders.ContentType, "image/${extension.removePrefix(".")}")
                })
            }
        )
        Log.d(TAG, "uploadImage() - Status: ${response.status.value}")
        return response.body()
    }
}
