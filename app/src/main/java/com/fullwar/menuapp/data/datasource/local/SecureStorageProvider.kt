package com.fullwar.menuapp.data.datasource.local

/**
 * Abstracción de almacenamiento seguro de key-value.
 *
 * En KMP, esta interfaz se convertiría en:
 *   expect class SecureStorageProvider { fun getString(...): String?; fun putString(...); ... }
 * Con implementaciones:
 *   - Android (actual): EncryptedSharedPreferences + Keystore
 *   - iOS (actual): Keychain Services
 */
interface SecureStorageProvider {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
    fun clear()
}
