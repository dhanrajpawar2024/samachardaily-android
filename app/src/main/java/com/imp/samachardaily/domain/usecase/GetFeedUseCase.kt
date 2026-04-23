package com.imp.samachardaily.domain.usecase

import androidx.paging.PagingData
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(
        language: String,
        categoryId: String? = null,
        languages: Set<String> = setOf(language)
    ): Flow<PagingData<Article>> =
        feedRepository.getFeed(language, categoryId, languages)
}

