package com.fullwar.menuapp.data.datasource.remote

import android.util.Log
import com.fullwar.menuapp.data.model.MenuDiarioCreateRequestDto
import com.fullwar.menuapp.data.model.MenuDiarioDetailResponseDto
import com.fullwar.menuapp.data.model.MenuDiarioListItemResponseDto
import com.fullwar.menuapp.data.model.MenuDiarioUpdateRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

class MenuDiarioService(private val httpClient: HttpClient) {

    companion object {
        private const val TAG = "MenuDiarioService"
    }

    suspend fun createMenuDiario(request: MenuDiarioCreateRequestDto, imagenFile: File?): Int {
        val response = httpClient.submitFormWithBinaryData(
            url = "api/menu-diario",
            formData = formData {
                append("fecha", request.fecha)
                request.entradasIds.forEach { id ->
                    append("entradasIds", id.toString())
                }
                request.platos.forEachIndexed { i, plato ->
                    append("platos[$i].platoId", plato.platoId.toString())
                    plato.adicionalId?.let { append("platos[$i].adicionalId", it.toString()) }
                }
                request.menuImagenId?.let { append("menuImagenId", it.toString()) }
                imagenFile?.let { file ->
                    append("imagen", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"menu_preview.jpg\"")
                    })
                }
            }
        )
        Log.d(TAG, "createMenuDiario() - Status: ${response.status.value}")
        return response.body<JsonObject>()["id"]?.jsonPrimitive?.int ?: 0
    }

    suspend fun getMenusDiarios(busqueda: String?): List<MenuDiarioListItemResponseDto> =
        httpClient.get("api/menu-diario") {
            busqueda?.let { parameter("busqueda", it) }
        }.body()

    suspend fun getMenuDiarioById(id: Int): MenuDiarioDetailResponseDto =
        httpClient.get("api/menu-diario/$id").body()

    suspend fun updateMenuDiario(id: Int, request: MenuDiarioUpdateRequestDto, imagenFile: File?): Boolean {
        val response = httpClient.put("api/menu-diario/$id") {
            setBody(MultiPartFormDataContent(formData {
                append("id", request.id.toString())
                append("estadoId", request.estadoId.toString())
                request.entradasIds.forEach { entradaId -> append("entradasIds", entradaId.toString()) }
                request.platos.forEachIndexed { i, plato ->
                    append("platos[$i].platoId", plato.platoId.toString())
                    plato.adicionalId?.let { append("platos[$i].adicionalId", it.toString()) }
                }
                request.imagenId?.let { append("imagenId", it.toString()) }
                request.menuImagenId?.let { append("menuImagenId", it.toString()) }
                imagenFile?.let { file ->
                    append("imagen", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"menu_preview.jpg\"")
                    })
                }
            }))
        }
        Log.d(TAG, "updateMenuDiario() - Status: ${response.status.value}")
        return response.status.isSuccess()
    }

    suspend fun deleteMenuDiario(id: Int) {
        val response = httpClient.delete("api/menu-diario/$id")
        Log.d(TAG, "deleteMenuDiario() - Status: ${response.status.value}")
    }
}
