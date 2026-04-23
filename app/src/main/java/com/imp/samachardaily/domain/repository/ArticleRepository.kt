package com.imp.samachardaily.domain.repository

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.model.Interaction
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticleDetail(articleId: String): Flow<NetworkResult<Article>>
    suspend fun logInteraction(interaction: Interaction): NetworkResult<Unit>
}

