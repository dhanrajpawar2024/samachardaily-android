package com.imp.samachardaily.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Nested pagination object returned by the feed-service:
 * { "articles": [...], "pagination": { "page": 1, "limit": 20, "total": 50 } }
 */
@JsonClass(generateAdapter = true)
data class PaginationDto(
    @Json(name = "page")  val page: Int = 1,
    @Json(name = "limit") val limit: Int = 20,
    @Json(name = "total") val total: Int = 0
) {
    /** Derived: ceil(total / limit), minimum 1 */
    val totalPages: Int get() = if (limit > 0) maxOf(1, (total + limit - 1) / limit) else 1
}

@JsonClass(generateAdapter = true)
data class FeedResponseDto(
    @Json(name = "articles")    val articles: List<ArticleDto> = emptyList(),
    // Nested pagination (feed-service format)
    @Json(name = "pagination")  val pagination: PaginationDto? = null,
    // Flat fields (content-service / legacy format)
    @Json(name = "page")        val flatPage: Int = 1,
    @Json(name = "total_pages") val flatTotalPages: Int = 1,
    @Json(name = "total")       val flatTotal: Int = 0,
    @Json(name = "limit")       val flatLimit: Int = 20
) {
    val page: Int       get() = pagination?.page       ?: flatPage
    val totalPages: Int get() = pagination?.totalPages ?: flatTotalPages
    val total: Int      get() = pagination?.total      ?: flatTotal
}

@JsonClass(generateAdapter = true)
data class NotificationItemDto(
    @Json(name = "id")           val id: String,
    @Json(name = "title")        val title: String,
    @Json(name = "body")         val body: String,
    @Json(name = "type")         val type: String,
    @Json(name = "payload")      val payload: Map<String, String>? = null,
    @Json(name = "is_read")      val isRead: Boolean = false,
    @Json(name = "created_at")   val createdAt: String
)

@JsonClass(generateAdapter = true)
data class NotificationListDto(
    @Json(name = "notifications") val notifications: List<NotificationItemDto> = emptyList(),
    @Json(name = "page")          val page: Int = 1,
    @Json(name = "total_pages")   val totalPages: Int = 1,
    @Json(name = "total")         val total: Int = 0
)
