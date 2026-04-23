package com.imp.samachardaily.core.database

import androidx.room.*

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKeys: List<ArticleRemoteKeyEntity>)

    @Query("SELECT * FROM article_remote_keys WHERE articleId = :articleId AND language = :language LIMIT 1")
    suspend fun getRemoteKeyByArticleId(
        articleId: String,
        language: String
    ): ArticleRemoteKeyEntity?

    @Query("DELETE FROM article_remote_keys WHERE language = :language")
    suspend fun clearRemoteKeysByLanguage(language: String)

    @Query("DELETE FROM article_remote_keys")
    suspend fun clearAll()
}

