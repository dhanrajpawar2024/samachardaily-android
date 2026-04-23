package com.imp.samachardaily.domain.repository

import androidx.paging.PagingData
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.core.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeed(language: String, categoryId: String? = null, languages: Set<String> = setOf(language)): Flow<PagingData<Article>>
    suspend fun getTrending(language: String, limit: Int = 10): NetworkResult<List<Article>>
}

