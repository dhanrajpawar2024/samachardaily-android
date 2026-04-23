package com.imp.samachardaily.data.repository

import com.imp.samachardaily.core.database.AppDatabase
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.core.network.safeApiCall
import com.imp.samachardaily.data.local.mapper.toDomain
import com.imp.samachardaily.data.local.mapper.toEntity
import com.imp.samachardaily.data.remote.NewsApiService
import com.imp.samachardaily.domain.model.Category
import com.imp.samachardaily.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val database: AppDatabase
) : CategoryRepository {

    override fun getCategories(language: String): Flow<NetworkResult<List<Category>>> = flow {
        emit(NetworkResult.Loading)

        val cached = database.categoryDao().getCategoriesByLanguage(language).first()
        if (cached.isNotEmpty()) {
            emit(NetworkResult.Success(cached.map { it.toDomain() }))
        }

        val result = safeApiCall { apiService.getCategories() }
        when (result) {
            is NetworkResult.Success -> {
                val payload = result.data
                if (payload.success && payload.data != null) {
                    val categories = payload.data.categories
                        .filter { it.isActive && it.language == language }

                    database.categoryDao().clearCategoriesByLanguage(language)
                    database.categoryDao().insertCategories(categories.map { it.toEntity() })
                    emit(NetworkResult.Success(categories.map { it.toDomain() }))
                } else {
                    emit(NetworkResult.Error(message = payload.error ?: "Failed to fetch categories"))
                }
            }
            is NetworkResult.Error -> emit(result)
            NetworkResult.Loading -> Unit
        }
    }
}

