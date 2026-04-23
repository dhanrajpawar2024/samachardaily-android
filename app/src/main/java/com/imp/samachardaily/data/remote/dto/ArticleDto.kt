package com.imp.samachardaily.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArticleDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "summary") val summary: String,
    @Json(name = "content") val content: String? = null,
    @Json(name = "author") val author: String? = null,
    @Json(name = "source_url") val sourceUrl: String,
    @Json(name = "source_name") val sourceName: String,
    @Json(name = "category_id") val categoryId: String,
    @Json(name = "language") val language: String,
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "published_at") val publishedAt: String,
    @Json(name = "view_count") val viewCount: Int = 0,
    @Json(name = "like_count") val likeCount: Int = 0,
    @Json(name = "share_count") val shareCount: Int = 0,
    @Json(name = "is_premium") val isPremium: Boolean = false
)

