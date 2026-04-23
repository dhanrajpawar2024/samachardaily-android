package com.imp.samachardaily.core.common

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/** Formats epoch millis to a human-readable relative time string. */
fun Long.toRelativeTimeString(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
        else -> {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            sdf.format(Date(this))
        }
    }
}

/** Parses an ISO-8601 date string to epoch millis. */
fun String.parseIso8601(): Long {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            java.time.Instant.parse(this).toEpochMilli()
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(this)?.time ?: System.currentTimeMillis()
        }
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

/** Formats a number to a compact representation (e.g. 1200 -> "1.2K") */
fun Int.toCompactString(): String = when {
    this >= 1_000_000 -> String.format("%.1fM", this / 1_000_000.0)
    this >= 1_000 -> String.format("%.1fK", this / 1_000.0)
    else -> this.toString()
}

/** Returns the display name for a language code. */
fun String.toLanguageDisplayName(): String = when (this) {
    "en" -> "English"
    "hi" -> "हिन्दी"
    "mr" -> "मराठी"
    "ta" -> "தமிழ்"
    "te" -> "తెలుగు"
    "bn" -> "বাংলা"
    "gu" -> "ગુજરાતી"
    "kn" -> "ಕನ್ನಡ"
    "pa" -> "ਪੰਜਾਬੀ"
    else -> this
}

