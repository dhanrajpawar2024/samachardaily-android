package com.imp.samachardaily.domain.model

/** Sealed wrapper used in the feed list — allows articles and inline ad slots. */
sealed class FeedItem {
    data class ArticleItem(val article: Article) : FeedItem()
    data class AdItem(
        val adUnitId: String,
        val positionKey: String
    ) : FeedItem()
}

