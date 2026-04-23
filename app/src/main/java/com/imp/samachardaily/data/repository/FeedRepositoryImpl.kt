package com.imp.samachardaily.data.repository

import androidx.paging.*
import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.core.database.AppDatabase
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.data.local.mapper.toDomain
import com.imp.samachardaily.data.remote.NewsApiService
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val database: AppDatabase
) : FeedRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getFeed(language: String, categoryId: String?, languages: Set<String>): Flow<PagingData<Article>> {
        val primaryLang = language.ifBlank { languages.firstOrNull() ?: "en" }
        val langsJoined = languages.joinToString(",").ifBlank { primaryLang }
        return Pager(
            config = PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE,
                prefetchDistance = Constants.PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            remoteMediator = FeedRemoteMediator(primaryLang, langsJoined, categoryId, apiService, database),
            pagingSourceFactory = {
                if (categoryId != null) {
                    database.articleDao().getArticlesByCategoryAndLanguage(primaryLang, categoryId)
                } else {
                    database.articleDao().getArticlesByLanguage(primaryLang)
                }
            }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override suspend fun getTrending(language: String, limit: Int): NetworkResult<List<Article>> {
        return try {
            val response = apiService.getTrendingFeed(page = 1, limit = limit, language = language)
            val articles = response.data?.articles?.map { it.toDomain() } ?: emptyList()
            NetworkResult.Success(articles)
        } catch (e: Exception) {
            NetworkResult.Error(message = e.message ?: "Failed to fetch trending")
        }
    }
}

