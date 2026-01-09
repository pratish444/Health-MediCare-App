package com.example.healthmedicareapp.data.local.dao

import androidx.room.*
import com.example.healthmedicareapp.data.local.entities.HealthArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<HealthArticleEntity>)

    @Query("SELECT * FROM health_articles ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<HealthArticleEntity>>

    @Query("DELETE FROM health_articles WHERE cachedAt < :expiryTime")
    suspend fun deleteExpiredArticles(expiryTime: Long)
}