package com.imp.samachardaily.domain.usecase

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Category
import com.imp.samachardaily.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(language: String): Flow<NetworkResult<List<Category>>> =
        categoryRepository.getCategories(language)
}

