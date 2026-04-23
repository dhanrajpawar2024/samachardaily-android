package com.imp.samachardaily.domain.usecase

import androidx.paging.PagingData
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(
        query: String,
        categoryId: String? = null,
        language: String? = null
    ): Flow<PagingData<Article>> = searchRepository.search(query, categoryId, language)
}

