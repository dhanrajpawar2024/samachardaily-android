package com.imp.samachardaily.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_remote_keys")
data class ArticleRemoteKeyEntity(
    @PrimaryKey val articleId: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val language: String,
    val categoryId: String?
)

