package com.fullwar.menuapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fullwar.menuapp.data.datasource.local.SecureStorageProvider
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Implementación moderna de almacenamiento seguro usando DataStore + Tink.
 * Reemplaza la implementación deprecada de EncryptedSharedPreferences.
 */
class SecureDataStoreImpl(private val context: Context) : SecureStorageProvider {

    companion object {
        private const val DATASTORE_NAME = "auth_secure_datastore"
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"

        private val Context.secureDataStore: DataStore<Preferences> by preferencesDataStore(
            name = DATASTORE_NAME
        )
    }

    private val aead: Aead by lazy {
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
        keysetHandle.getPrimitive(Aead::class.java)
    }

    override fun getString(key: String): String? {
        return runBlocking {
            try {
                val prefKey = stringPreferencesKey(key)
                val encryptedValue = context.secureDataStore.data
                    .map { preferences -> preferences[prefKey] }
                    .first()

                encryptedValue?.let { decrypt(it) }
            } catch (e: Exception) {
                // Log error si es necesario
                null
            }
        }
    }

    override fun putString(key: String, value: String) {
        runBlocking {
            try {
                val prefKey = stringPreferencesKey(key)
                val encryptedValue = encrypt(value)

                context.secureDataStore.edit { preferences ->
                    preferences[prefKey] = encryptedValue
                }
            } catch (e: Exception) {
                // Log error si es necesario
                throw e
            }
        }
    }

    override fun remove(key: String) {
        runBlocking {
            try {
                val prefKey = stringPreferencesKey(key)
                context.secureDataStore.edit { preferences ->
                    preferences.remove(prefKey)
                }
            } catch (e: Exception) {
                // Log error si es necesario
                throw e
            }
        }
    }

    override fun clear() {
        runBlocking {
            try {
                context.secureDataStore.edit { preferences ->
                    preferences.clear()
                }
            } catch (e: Exception) {
                // Log error si es necesario
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
