package com.imp.samachardaily.core.common

object Constants {
    // API
    const val BASE_URL = "http://192.168.31.132:3000/"
    const val API_VERSION = "v1"
    const val DEFAULT_PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5

    // Supported languages
    const val LANG_ENGLISH  = "en"
    const val LANG_HINDI    = "hi"
    const val LANG_MARATHI  = "mr"
    const val LANG_TAMIL    = "ta"
    const val LANG_TELUGU   = "te"
    const val LANG_BENGALI  = "bn"
    const val LANG_GUJARATI = "gu"
    const val LANG_KANNADA  = "kn"
    const val LANG_PUNJABI  = "pa"

    val SUPPORTED_LANGUAGES = listOf(
        LANG_ENGLISH, LANG_HINDI, LANG_MARATHI,
        LANG_TAMIL, LANG_TELUGU, LANG_BENGALI,
        LANG_GUJARATI, LANG_KANNADA, LANG_PUNJABI
    )

    // Cache TTL
    const val FEED_CACHE_TTL_MS = 15 * 60 * 1000L  // 15 min
    const val CATEGORIES_CACHE_TTL_MS = 60 * 60 * 1000L // 1 hour

    // AdMob
    const val ADMOB_APP_ID = "ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"
    const val AD_FEED_INTERVAL = 5  // Show ad every N articles

    // Deep link scheme
    const val DEEP_LINK_SCHEME = "samachardaily"
    const val DEEP_LINK_HOST = "app"

    // FCM notification types
    const val NOTIF_TYPE_ARTICLE = "article"
    const val NOTIF_TYPE_VIDEO = "video"
    const val NOTIF_TYPE_BREAKING = "breaking"
    const val NOTIF_TYPE_PROMO = "promo"
}
