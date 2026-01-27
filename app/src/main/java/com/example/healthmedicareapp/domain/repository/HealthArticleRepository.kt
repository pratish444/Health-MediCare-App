package com.example.healthmedicareapp.domain.repository

import com.example.healthmedicareapp.domain.model.HealthArticle
import kotlinx.coroutines.flow.Flow

interface HealthArticleRepository {
    fun getHealthArticles(): Flow<List<HealthArticle>>
    suspend fun refreshArticles(): Result<Unit>
}