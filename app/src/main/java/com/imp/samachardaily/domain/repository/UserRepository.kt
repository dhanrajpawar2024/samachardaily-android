package com.imp.samachardaily.domain.repository

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<NetworkResult<User>>
    suspend fun loginWithGoogle(idToken: String): NetworkResult<User>
    suspend fun logout(): NetworkResult<Unit>
    suspend fun registerFcmToken(fcmToken: String): NetworkResult<Unit>
    suspend fun updatePreferredLanguages(languages: List<String>): NetworkResult<Unit>
}

