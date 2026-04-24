package com.imp.samachardaily.data.remote

import com.imp.samachardaily.data.remote.dto.*
import retrofit2.http.*

interface NewsApiService {

    // ── Auth (no bearer token required) ─────────────────────
    @POST("api/v1/auth/google")
    suspend fun loginWithGoogle(@Body body: AuthRequestDto): ApiResponse<AuthResponseDto>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body body: RefreshTokenRequestDto): ApiResponse<AuthResponseDto>

    @POST("api/v1/auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @GET("api/v1/auth/me")
    suspend fun getCurrentUser(): ApiResponse<CurrentUserResponseDto>

    @GET("api/v1/bookmarks")
    suspend fun getBookmarks(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): ApiResponse<BookmarksResponseDto>

    @POST("api/v1/bookmarks")
    suspend fun addBookmark(@Body body: BookmarkRequestDto): ApiResponse<Unit>

    @DELETE("api/v1/bookmarks/{articleId}")
    suspend fun removeBookmark(@Path("articleId") articleId: String): ApiResponse<Unit>

    // ── Articles ─────────────────────────────────────────────
    @GET("api/v1/articles")
    suspend fun getArticles(
        @Query("page")     page: Int     = 1,
        @Query("limit")    limit: Int    = 20,
        @Query("language") language: String? = null,
        @Query("category") category: String? = null
    ): ApiResponse<FeedResponseDto>

    @GET("api/v1/categories")
    suspend fun getCategories(): ApiResponse<CategoriesResponseDto>

    @GET("api/v1/articles/{id}")
    suspend fun getArticleById(@Path("id") id: String): ApiResponse<ArticleDetailResponseDto>

    @POST("api/v1/articles/{id}/like")
    suspend fun likeArticle(@Path("id") id: String): ApiResponse<Unit>


    @GET("api/v1/articles/{id}/interactions")
    suspend fun getArticleInteractions(@Path("id") id: String): ApiResponse<Unit>

    // ── Feed ─────────────────────────────────────────────────
    @GET("api/v1/feed")
    suspend fun getFeed(
        @Query("page")      page: Int      = 1,
        @Query("limit")     limit: Int     = 20,
        @Query("language")  language: String? = null,
        @Query("languages") languages: String? = null  // comma-separated e.g. "en,hi,mr"
    ): ApiResponse<FeedResponseDto>

    @GET("api/v1/feed/trending")
    suspend fun getTrendingFeed(
        @Query("page")     page: Int     = 1,
        @Query("limit")    limit: Int    = 20,
        @Query("language") language: String? = null
    ): ApiResponse<FeedResponseDto>

    @GET("api/v1/feed/category/{categoryId}")
    suspend fun getFeedByCategory(
        @Path("categoryId") categoryId: String,
        @Query("page")      page: Int     = 1,
        @Query("limit")     limit: Int    = 20,
        @Query("language")  language: String? = null
    ): ApiResponse<FeedResponseDto>

    // ── Search ───────────────────────────────────────────────
    @GET("api/v1/search")
    suspend fun search(
        @Query("q")           query: String,
        @Query("language")    language: String? = null,
        @Query("category_id") categoryId: String? = null,
        @Query("page")        page: Int     = 1,
        @Query("limit")       limit: Int    = 20
    ): ApiResponse<SearchResultDto>

    @GET("api/v1/search/suggestions")
    suspend fun getSearchSuggestions(
        @Query("q") query: String,
        @Query("language") language: String? = null,
        @Query("limit") limit: Int = 10
    ): ApiResponse<SearchSuggestionsDto>

    @GET("api/v1/search/trending")
    suspend fun getTrendingKeywords(
        @Query("language") language: String? = null,
        @Query("limit") limit: Int = 20
    ): ApiResponse<TrendingKeywordsDto>

    @GET("api/v1/search/filters")
    suspend fun getSearchFilters(
        @Query("language") language: String? = null
    ): ApiResponse<SearchFiltersDto>

    // ── Recommendations ──────────────────────────────────────
    @GET("api/v1/recommendations/for-user")
    suspend fun getRecommendationsForUser(
        @Query("page")  page: Int  = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<FeedResponseDto>

    @GET("api/v1/recommendations/similar/{articleId}")
    suspend fun getSimilarArticles(
        @Path("articleId") articleId: String,
        @Query("limit")    limit: Int = 10
    ): ApiResponse<FeedResponseDto>

    @GET("api/v1/recommendations/trending")
    suspend fun getRecommendationsTrending(
        @Query("language") language: String? = null,
        @Query("limit")    limit: Int = 20
    ): ApiResponse<FeedResponseDto>

    @POST("api/v1/recommendations/feedback")
    suspend fun sendRecommendationFeedback(
        @Body body: RecommendationFeedbackDto
    ): ApiResponse<Unit>

    // ── Notifications ────────────────────────────────────────
    @POST("api/v1/notifications/register-token")
    suspend fun registerFcmToken(@Body body: FcmRegisterDto): ApiResponse<Unit>

    @DELETE("api/v1/notifications/unregister-token")
    suspend fun unregisterFcmToken(@Body body: FcmRegisterDto): ApiResponse<Unit>

    @GET("api/v1/notifications/history")
    suspend fun getNotificationHistory(
        @Query("page")  page: Int  = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<NotificationListDto>

    @POST("api/v1/notifications/mark-read/{notificationId}")
    suspend fun markNotificationRead(
        @Path("notificationId") notificationId: String
    ): ApiResponse<Unit>

    @GET("api/v1/notifications/preferences")
    suspend fun getNotificationPreferences(): ApiResponse<Unit>

    // ── Videos ──────────────────────────────────────────────
    @GET("api/v1/videos")
    suspend fun getVideos(
        @Query("page")        page: Int        = 1,
        @Query("limit")       limit: Int       = 20,
        @Query("language")    language: String? = null,
        @Query("category_id") categoryId: String? = null
    ): VideoListResponseDto

    @GET("api/v1/videos/{id}")
    suspend fun getVideoById(@Path("id") id: String): VideoDetailResponseDto

    @POST("api/v1/videos/{id}/view")
    suspend fun incrementVideoView(@Path("id") id: String): VideoActionResponseDto

    @POST("api/v1/videos/{id}/like")
    suspend fun likeVideo(@Path("id") id: String): VideoActionResponseDto

    // ── Health ───────────────────────────────────────────────
    @GET("health")
    suspend fun healthCheck(): Map<String, Any>
}
