package com.imp.samachardaily.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            NetworkResult.Success(apiCall())
        } catch (e: HttpException) {
            NetworkResult.Error(code = e.code(), message = e.message ?: "HTTP Error ${e.code()}")
        } catch (e: IOException) {
            NetworkResult.Error(message = "Network unavailable. Check your connection.")
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "An unexpected error occurred.")
        }
    }
}

