package com.imp.samachardaily.core.database

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles WHERE language = :language ORDER BY publishedAt DESC")
    fun getArticlesByLanguage(language: String): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE language = :language AND categoryId = :categoryId ORDER BY publishedAt DESC")
    fun getArticlesByCategoryAndLanguage(
        language: String,
        categoryId: String
    ): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    suspend fun getArticleById(id: String): ArticleEntity?

    @Query("SELECT * FROM articles WHERE language = :language ORDER BY publishedAt DESC LIMIT :limit")
    fun getRecentArticles(language: String, limit: Int = 10): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Query("DELETE FROM articles WHERE language = :language AND categoryId = :categoryId")
    suspend fun clearArticlesByCategoryAndLanguage(language: String, categoryId: String)

    @Query("DELETE FROM articles WHERE language = :language")
    suspend fun clearArticlesByLanguage(language: String)

    @Query("DELETE FROM articles")
    suspend fun clearAll()
}

