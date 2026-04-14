package com.fullwar.menuapp.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fullwar.menuapp.data.datasource.local.SecureStorageProvider
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementación moderna de almacenamiento seguro usando DataStore + Tink.
 * Reemplaza la implementación deprecada de EncryptedSharedPreferences.
 *
 * Todas las operaciones son suspendibles y no bloquean el hilo principal.
 */
class SecureDataStoreImpl(private val context: Context) : SecureStorageProvider {

    companion object {
        private const val TAG = "SecureDataStoreImpl"
        private const val DATASTORE_NAME = "auth_secure_datastore"
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"

        private val Context.secureDataStore: DataStore<Preferences> by preferencesDataStore(
            name = DATASTORE_NAME
        )
    }

    private val aead: Aead by lazy {
        val startTime = System.currentTimeMillis()

        // Inicializar Tink
        AeadConfig.register()

        // Crear o cargar el keyset usando Android Keystore
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle

        // Obtener el primitivo AEAD para encriptar/desencriptar
        val aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)

        val elapsed = System.currentTimeMillis() - startTime
        Log.d(TAG, "AEAD initialized in ${elapsed}ms")

        aead
    }

    override suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val prefKey = stringPreferencesKey(key)
            val encryptedValue = context.secureDataStore.data
                .map { preferences -> preferences[prefKey] }
                .first()

            val result = encryptedValue?.let { decrypt(it) }
            val elapsed = System.currentTimeMillis() - startTime
            Log.d(TAG, "getString('$key') took ${elapsed}ms")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error getting string for key '$key': ${e.message}", e)
            null
        }
    }

    override suspend fun putString(key: String, value: String) {
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            try {
                val prefKey = stringPreferencesKey(key)
                val encryptedValue = encrypt(value)

                context.secureDataStore.edit { preferences ->
                    preferences[prefKey] = encryptedValue
                }

                val elapsed = System.currentTimeMillis() - startTime
                Log.d(TAG, "putString('$key') took ${elapsed}ms")
            } catch (e: Exception) {
                Log.e(TAG, "Error putting string for key '$key': ${e.message}", e)
                throw e
            }
        }
    }

    override suspend fun remove(key: String) {
        withContext(Dispatchers.IO) {
            try {
                val prefKey = stringPreferencesKey(key)
                context.secureDataStore.edit { preferences ->
                    preferences.remove(prefKey)
                }
                Log.d(TAG, "remove('$key') completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing key '$key': ${e.message}", e)
                throw e
            }
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            try {
                context.secureDataStore.edit { preferences ->
                    preferences.clear()
                }
                Log.d(TAG, "clear() completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing storage: ${e.message}", e)
                throw e
            }
        }
    }

    /**
     * Encripta un string usando Tink AEAD
     */
    private fun encrypt(plaintext: String): String {
        val ciphertext = aead.encrypt(
            plaintext.toByteArray(Charsets.UTF_8),
            null // Associated data opcional
        )
        return android.util.Base64.encodeToString(ciphertext, android.util.Base64.NO_WRAP)
    }

    /**
     * Desencripta un string usando Tink AEAD
     */
    private fun decrypt(ciphertext: String): String {
        val encryptedBytes = android.util.Base64.decode(ciphertext, android.util.Base64.NO_WRAP)
        val plaintext = aead.decrypt(encryptedBytes, null)
        return String(plaintext, Charsets.UTF_8)
    }
}
