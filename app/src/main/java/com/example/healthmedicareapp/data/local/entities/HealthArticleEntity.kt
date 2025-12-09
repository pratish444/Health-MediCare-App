package com.example.healthmedicareapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_articles")
data class HealthArticleEntity(
    @PrimaryKey
    val url: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val publishedAt: String,
    val source: String,
    val cachedAt: Long = System.currentTimeMillis()
)