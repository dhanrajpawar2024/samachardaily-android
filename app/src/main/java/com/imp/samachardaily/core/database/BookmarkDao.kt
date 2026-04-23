package com.imp.samachardaily.core.database

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("""
        SELECT a.* FROM articles a 
        INNER JOIN bookmarks b ON a.id = b.articleId 
        WHERE b.userId = :userId 
        ORDER BY b.createdAt DESC
    """)
    fun getBookmarkedArticles(userId: String): PagingSource<Int, ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmarks(bookmarks: List<BookmarkEntity>)

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND articleId = :articleId")
    suspend fun removeBookmark(userId: String, articleId: String)

    @Query("DELETE FROM bookmarks WHERE userId = :userId")
    suspend fun clearBookmarksForUser(userId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND articleId = :articleId)")
    fun isBookmarked(userId: String, articleId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM bookmarks WHERE userId = :userId")
    fun getBookmarkCount(userId: String): Flow<Int>
}

