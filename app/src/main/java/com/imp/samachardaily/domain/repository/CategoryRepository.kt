package com.imp.samachardaily.domain.repository

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(language: String): Flow<NetworkResult<List<Category>>>
}

