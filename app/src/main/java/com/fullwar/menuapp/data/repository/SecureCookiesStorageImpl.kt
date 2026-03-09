package com.fullwar.menuapp.data.repository

import com.fullwar.menuapp.data.datasource.local.SecureStorageProvider
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.http.parseServerSetCookieHeader
import io.ktor.http.renderSetCookieHeader
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SecureCookiesStorageImpl(
    private val secureStorage: SecureStorageProvider
) : CookiesStorage {

    private val mutex = Mutex()
    private var cookies: MutableList<Cookie>? = null

    private suspend fun loadCookies(): MutableList<Cookie> {
        val stored = secureStorage.getString(COOKIES_KEY) ?: return mutableListOf()
        return stored.split(SEPARATOR)
            .filter { it.isNotBlank() }
            .mapNotNull { raw ->
                try {
                    parseServerSetCookieHeader(raw)
                } catch (e: Exception) {
                    null
                }
            }
            .toMutableList()
    }

    private suspend fun saveCookies(list: List<Cookie>) {
        val serialized = list.joinToString(SEPARATOR) { renderSetCookieHeader(it) }
        secureStorage.putString(COOKIES_KEY, serialized)
    }

    private suspend fun ensureLoaded(): MutableList<Cookie> {
        if (cookies == null) {
            cookies = loadCookies()
        }
        return cookies!!
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        mutex.withLock {
            val list = ensureLoaded()
            // Reemplazar cookie existente con mismo nombre y dominio
            list.removeAll { it.name == cookie.name && it.domain == cookie.domain }
            list.add(cookie)
            // Eliminar cookies expiradas
            val now = GMTDate()
            list.removeAll { it.expires?.let { exp -> exp < now } == true }
            saveCookies(list)
        }
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        mutex.withLock {
            val list = ensureLoaded()
            val now = GMTDate()
            // Eliminar cookies expiradas
            list.removeAll { it.expires?.let { exp -> exp < now } == true }
            saveCookies(list)
            // Retornar cookies que coincidan con el dominio de la URL
            return list.filter { cookie ->
                val cookieDomain = cookie.domain ?: return@filter true
                requestUrl.host.endsWith(cookieDomain) || requestUrl.host == cookieDomain
            }
        }
    }

    override fun close() {
        // No-op
    }

    /** Limpia todas las cookies almacenadas (usado en logout) */
    suspend fun clear() {
        cookies = mutableListOf()
        secureStorage.remove(COOKIES_KEY)
    }

    companion object {
        private const val COOKIES_KEY = "ktor_cookies"
        private const val SEPARATOR = "|||"
    }
}
