package com.imp.samachardaily.core.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProvider @Inject constructor() {
    @Volatile private var accessToken: String? = null
    @Volatile private var refreshToken: String? = null

    fun getToken(): String?        = accessToken
    fun getRefreshToken(): String? = refreshToken

    fun setTokens(access: String, refresh: String) {
        accessToken  = access
        refreshToken = refresh
    }

    fun setToken(newToken: String?)    { accessToken  = newToken }
    fun setRefreshToken(token: String?) { refreshToken = token }

    fun clearToken() {
        accessToken  = null
        refreshToken = null
    }

    fun hasToken(): Boolean = accessToken != null
}
