package com.imp.samachardaily.domain.usecase

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.repository.FeedRepository
import javax.inject.Inject

class GetTrendingUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(language: String, limit: Int = 10): NetworkResult<List<Article>> =
        feedRepository.getTrending(language, limit)
}

