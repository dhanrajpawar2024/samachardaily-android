package com.imp.samachardaily.data.repository

import com.imp.samachardaily.core.datastore.UserPreferencesDataStore
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.core.network.TokenProvider
import com.imp.samachardaily.core.network.safeApiCall
import com.imp.samachardaily.data.local.mapper.toDomain
import com.imp.samachardaily.data.remote.NewsApiService
import com.imp.samachardaily.data.remote.dto.AuthRequestDto
import com.imp.samachardaily.data.remote.dto.FcmRegisterDto
import com.imp.samachardaily.domain.model.User
import com.imp.samachardaily.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val tokenProvider: TokenProvider
) : UserRepository {

    override fun getCurrentUser(): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        when (val result = safeApiCall { apiService.getCurrentUser() }) {
            is NetworkResult.Success -> {
                val body = result.data
                if (body.success && body.data != null) {
                    emit(NetworkResult.Success(body.data.user.toDomain()))
                } else {
                    emit(NetworkResult.Error(message = body.error ?: "Failed to load profile"))
                }
            }
            is NetworkResult.Error -> emit(result)
            NetworkResult.Loading -> emit(NetworkResult.Loading)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): NetworkResult<User> {
        val result = safeApiCall { apiService.loginWithGoogle(AuthRequestDto(idToken)) }
        return when (result) {
            is NetworkResult.Success -> {
                val body = result.data
                if (body.success && body.data != null) {
                    val tokens = body.data.tokens
                    val userDto = body.data.user
                    // Persist tokens
                    tokenProvider.setTokens(tokens.accessToken, tokens.refreshToken)
                    userPreferencesDataStore.setAuthToken(tokens.accessToken)
                    userPreferencesDataStore.setRefreshToken(tokens.refreshToken)
                    userPreferencesDataStore.setUserId(userDto.id)
                    NetworkResult.Success(userDto.toDomain())
                } else {
                    NetworkResult.Error(message = body.error ?: "Login failed")
                }
            }
            is NetworkResult.Error -> result
            NetworkResult.Loading  -> NetworkResult.Loading
        }
    }

    override suspend fun logout(): NetworkResult<Unit> {
        safeApiCall { apiService.logout() }   // best-effort server logout
        tokenProvider.clearToken()
        userPreferencesDataStore.clearUserData()
        return NetworkResult.Success(Unit)
    }

    override suspend fun registerFcmToken(fcmToken: String): NetworkResult<Unit> {
        val result = safeApiCall {
            apiService.registerFcmToken(FcmRegisterDto(token = fcmToken))
        }
        return when (result) {
            is NetworkResult.Success -> if (result.data.success) NetworkResult.Success(Unit)
                                        else NetworkResult.Error(message = result.data.error ?: "Failed")
            is NetworkResult.Error   -> result
            NetworkResult.Loading    -> NetworkResult.Loading
        }
    }

    override suspend fun updatePreferredLanguages(languages: List<String>): NetworkResult<Unit> {
        userPreferencesDataStore.setSelectedCategories(languages)
        return NetworkResult.Success(Unit)
    }
}
