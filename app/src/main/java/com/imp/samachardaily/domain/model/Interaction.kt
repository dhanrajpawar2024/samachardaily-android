package com.imp.samachardaily.domain.model

data class Interaction(
    val articleId: String,
    val action: InteractionAction,
    val durationSeconds: Int? = null
)

enum class InteractionAction(val value: String) {
    VIEW("view"),
    LIKE("like"),
    SHARE("share"),
    BOOKMARK("bookmark"),
    SKIP("skip")
}

