package com.imp.samachardaily.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Generic API envelope ─────────────────────────────────────
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data")    val data: T? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "error")   val error: String? = null
)

@JsonClass(generateAdapter = true)
data class CategoriesResponseDto(
    @Json(name = "categories") val categories: List<CategoryDto> = emptyList()
)

// ── Auth ─────────────────────────────────────────────────────
@JsonClass(generateAdapter = true)
data class AuthRequestDto(
    @Json(name = "idToken") val idToken: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequestDto(
    @Json(name = "refreshToken") val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "user")   val user: UserDto,
    @Json(name = "tokens") val tokens: TokensDto
)

@JsonClass(generateAdapter = true)
data class CurrentUserResponseDto(
    @Json(name = "user") val user: UserDto
)

@JsonClass(generateAdapter = true)
data class BookmarkRequestDto(
    @Json(name = "articleId") val articleId: String
)

@JsonClass(generateAdapter = true)
data class BookmarksResponseDto(
    @Json(name = "bookmarks") val bookmarks: List<ArticleDto> = emptyList(),
    @Json(name = "page") val page: Int = 1,
    @Json(name = "limit") val limit: Int = 20
)

@JsonClass(generateAdapter = true)
data class ArticleDetailResponseDto(
    @Json(name = "article") val article: ArticleDto
)

@JsonClass(generateAdapter = true)
data class TokensDto(
    @Json(name = "accessToken")  val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String,
    @Json(name = "expiresIn")    val expiresIn: Int   // seconds
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")        val id: String,
    @Json(name = "email")     val email: String? = null,
    @Json(name = "phone")     val phone: String? = null,
    @Json(name = "name")      val name: String,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "preferred_languages") val preferredLanguages: List<String> = emptyList(),
    @Json(name = "created_at")          val createdAt: String? = null
)

// ── Interaction ──────────────────────────────────────────────
@JsonClass(generateAdapter = true)
data class InteractionRequestDto(
    @Json(name = "article_id")       val articleId: String,
    @Json(name = "action")           val action: String,
    @Json(name = "duration_seconds") val durationSeconds: Int? = null
)

// ── FCM ──────────────────────────────────────────────────────
@JsonClass(generateAdapter = true)
data class FcmRegisterDto(
    @Json(name = "token")    val token: String,
    @Json(name = "platform") val platform: String = "android"
)

// ── Recommendation feedback ──────────────────────────────────
@JsonClass(generateAdapter = true)
data class RecommendationFeedbackDto(
    @Json(name = "article_id") val articleId: String,
    @Json(name = "action")     val action: String   // "like" | "skip" | "save"
)
