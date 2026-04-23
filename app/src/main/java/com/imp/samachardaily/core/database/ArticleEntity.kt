package com.imp.samachardaily.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val content: String?,
    val author: String?,
    val sourceUrl: String,
    val sourceName: String,
    val categoryId: String,
    val language: String,
    val thumbnailUrl: String?,
    val publishedAt: Long,
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val shareCount: Int = 0,
    val isPremium: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)

