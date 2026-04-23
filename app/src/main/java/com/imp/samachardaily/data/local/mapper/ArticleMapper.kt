package com.imp.samachardaily.data.local.mapper

import com.imp.samachardaily.core.common.parseIso8601
import com.imp.samachardaily.core.common.toRelativeTimeString
import com.imp.samachardaily.core.database.ArticleEntity
import com.imp.samachardaily.data.remote.dto.ArticleDto
import com.imp.samachardaily.domain.model.Article

// DTO → Domain
fun ArticleDto.toDomain(): Article = Article(
    id = id,
    title = title,
    summary = summary,
    content = content,
    author = author,
    sourceUrl = sourceUrl,
    sourceName = sourceName,
    categoryId = categoryId,
    language = language,
    thumbnailUrl = thumbnailUrl,
    publishedAt = publishedAt.parseIso8601(),
    viewCount = viewCount,
    likeCount = likeCount,
    shareCount = shareCount,
    isPremium = isPremium
)

// DTO → Entity (for Room cache)
fun ArticleDto.toEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title,
    summary = summary,
    content = content,
    author = author,
    sourceUrl = sourceUrl,
    sourceName = sourceName,
    categoryId = categoryId,
    language = language,
    thumbnailUrl = thumbnailUrl,
    publishedAt = publishedAt.parseIso8601(),
    viewCount = viewCount,
    likeCount = likeCount,
    shareCount = shareCount,
    isPremium = isPremium
)

// Entity → Domain
fun ArticleEntity.toDomain(): Article = Article(
    id = id,
    title = title,
    summary = summary,
    content = content,
    author = author,
    sourceUrl = sourceUrl,
    sourceName = sourceName,
    categoryId = categoryId,
    language = language,
    thumbnailUrl = thumbnailUrl,
    publishedAt = publishedAt,
    viewCount = viewCount,
    likeCount = likeCount,
    shareCount = shareCount,
    isPremium = isPremium
)

