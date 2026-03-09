package com.fullwar.menuapp.data.datasource.local

/**
 * Abstracción de almacenamiento seguro de key-value.
 *
 * Todas las operaciones son suspendibles para no bloquear el hilo principal.
 *
 * En KMP, esta interfaz se convertiría en:
 *   expect class SecureStorageProvider { suspend fun getString(...): String?; suspend fun putString(...); ... }
 * Con implementaciones:
 *   - Android (actual): DataStore + Tink + Android Keystore
 *   - iOS (actual): Keychain Services
 */
interface SecureStorageProvider {
    suspend fun getString(key: String): String?
    suspend fun putString(key: String, value: String)
    suspend fun remove(key: String)
    suspend fun clear()
}
