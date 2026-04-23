package com.imp.samachardaily.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.imp.samachardaily.core.database.AppDatabase
import com.imp.samachardaily.core.database.ArticleEntity
import com.imp.samachardaily.core.database.ArticleRemoteKeyEntity
import com.imp.samachardaily.data.local.mapper.toEntity
import com.imp.samachardaily.data.remote.NewsApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator(
    private val language: String,          // primary language for Room cache key
    private val languages: String,         // comma-separated all selected e.g. "en,hi,mr"
    private val categoryId: String?,
    private val apiService: NewsApiService,
    private val database: AppDatabase
) : RemoteMediator<Int, ArticleEntity>() {

    private val articleDao = database.articleDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun initialize(): InitializeAction =
        InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    val remoteKey = remoteKeyDao.getRemoteKeyByArticleId(lastItem.id, language)
                    remoteKey?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = apiService.getFeed(
                page = page,
                limit = state.config.pageSize,
                language = language,
                languages = languages.ifBlank { null }
            )

            if (!response.success || response.data == null) {
                return MediatorResult.Error(Exception(response.error ?: "Feed fetch failed"))
            }

            val feedData = response.data
            val articles = if (categoryId != null) {
                feedData.articles.filter { it.categoryId == categoryId }
            } else {
                feedData.articles
            }

            val endOfPaginationReached = page >= feedData.totalPages

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clearRemoteKeysByLanguage(language)
                    if (categoryId != null) {
                        articleDao.clearArticlesByCategoryAndLanguage(language, categoryId)
                    } else {
                        articleDao.clearArticlesByLanguage(language)
                    }
                }

                val remoteKeys = articles.map { dto ->
                    ArticleRemoteKeyEntity(
                        articleId = dto.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1,
                        language = language,
                        categoryId = categoryId
                    )
                }

                remoteKeyDao.insertOrReplace(remoteKeys)
                articleDao.insertArticles(articles.map { it.toEntity() })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

