package com.imp.samachardaily.core.network

import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.core.datastore.UserPreferencesDataStore
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Authenticator — fires synchronously on every 401 response.
 * Attempts a token refresh once; if it fails, clears all tokens.
 */
@Singleton
class TokenRefreshAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent refresh loop — if we already tried once, give up
        if (response.request.header("X-Retry-After-Refresh") != null) return null

        val refreshToken = tokenProvider.getRefreshToken() ?: run {
            tokenProvider.clearToken()
            return null
        }

        // Perform a synchronous token refresh using a bare OkHttpClient
        // (cannot use the injected Retrofit client — would cause circular dependency)
        return try {
            val body = """{"refreshToken":"$refreshToken"}"""
                .toRequestBody("application/json".toMediaType())

            val refreshResponse = OkHttpClient().newCall(
                Request.Builder()
                    .url("${Constants.BASE_URL}api/v1/auth/refresh")
                    .post(body)
                    .build()
            ).execute()

            if (refreshResponse.isSuccessful) {
                val json = JSONObject(refreshResponse.body?.string() ?: "")
                val data = json.optJSONObject("data")
                val tokens = data?.optJSONObject("tokens")
                val nestedAccess = tokens?.optString("accessToken").orEmpty()
                val topLevelAccess = data?.optString("accessToken").orEmpty()
                val nestedRefresh = tokens?.optString("refreshToken").orEmpty()
                val topLevelRefresh = data?.optString("refreshToken").orEmpty()
                val newAccess = when {
                    nestedAccess.isNotBlank() -> nestedAccess
                    topLevelAccess.isNotBlank() -> topLevelAccess
                    else -> null
                }
                val newRefresh = when {
                    nestedRefresh.isNotBlank() -> nestedRefresh
                    topLevelRefresh.isNotBlank() -> topLevelRefresh
                    else -> refreshToken
                }

                if (!newAccess.isNullOrBlank() && !newRefresh.isNullOrBlank()) {
                    tokenProvider.setTokens(newAccess, newRefresh)
                    runBlocking {
                        userPreferencesDataStore.setAuthToken(newAccess)
                        userPreferencesDataStore.setRefreshToken(newRefresh)
                    }
                    // Retry the original request with the new access token
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccess")
                        .header("X-Retry-After-Refresh", "true")
                        .build()
                } else {
                    tokenProvider.clearToken()
                    runBlocking { userPreferencesDataStore.clearUserData() }
                    null
                }
            } else {
                tokenProvider.clearToken()
                runBlocking { userPreferencesDataStore.clearUserData() }
                null
            }
        } catch (_: Exception) {
            tokenProvider.clearToken()
            runBlocking { userPreferencesDataStore.clearUserData() }
            null
        }
    }
}

