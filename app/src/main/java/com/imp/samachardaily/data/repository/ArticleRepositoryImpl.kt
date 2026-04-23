package com.imp.samachardaily.data.repository

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.core.network.safeApiCall
import com.imp.samachardaily.data.local.mapper.toDomain
import com.imp.samachardaily.data.remote.NewsApiService
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.model.Interaction
import com.imp.samachardaily.domain.model.InteractionAction
import com.imp.samachardaily.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService
) : ArticleRepository {

    override fun getArticleDetail(articleId: String): Flow<NetworkResult<Article>> = flow {
        emit(NetworkResult.Loading)
        when (val result = safeApiCall { apiService.getArticleById(articleId) }) {
            is NetworkResult.Success -> {
                val body = result.data
                if (body.success && body.data != null) {
                    emit(NetworkResult.Success(body.data.article.toDomain()))
                } else {
                    emit(NetworkResult.Error(message = body.error ?: "Article not found"))
                }
            }
            is NetworkResult.Error -> emit(result)
            NetworkResult.Loading  -> emit(NetworkResult.Loading)
        }
    }

    override suspend fun logInteraction(interaction: Interaction): NetworkResult<Unit> {
        // The backend tracks engagement via dedicated action endpoints.
        // Route "like" to /articles/:id/like; others are fire-and-forget no-ops
        // until a dedicated /interactions endpoint is added to the content service.
        return when (interaction.action) {
            InteractionAction.LIKE -> {
                val result = safeApiCall { apiService.likeArticle(interaction.articleId) }
                when (result) {
                    is NetworkResult.Success -> if (result.data.success) NetworkResult.Success(Unit)
                                               else NetworkResult.Error(message = result.data.error ?: "Like failed")
                    is NetworkResult.Error   -> result
                    NetworkResult.Loading    -> NetworkResult.Loading
                }
            }
            else -> NetworkResult.Success(Unit)   // view/share/skip logged client-side only for now
        }
    }
}

