package com.imp.samachardaily.presentation.navigation

sealed class Screen(val route: String) {
    // Root
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")

    // Main bottom-nav destinations
    object Home : Screen("home")
    object Search : Screen("search")
    object ShortVideo : Screen("short_video")
    object Profile : Screen("profile")
    object Bookmarks : Screen("bookmarks")

    // Detail screens
    object ArticleDetail : Screen("article/{articleId}") {
        fun createRoute(articleId: String) = "article/$articleId"
    }

    // Deep-link friendly notification target
    object NotificationList : Screen("notifications")

    companion object {
        const val BOTTOM_NAV_HOME = "home"
        const val BOTTOM_NAV_SEARCH = "search"
        const val BOTTOM_NAV_VIDEO = "short_video"
        const val BOTTOM_NAV_PROFILE = "profile"
    }
}

