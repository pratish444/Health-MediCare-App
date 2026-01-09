package com.example.healthmedicareapp.domain.repository

import com.example.healthmedicareapp.domain.model.HealthArticle
import kotlinx.coroutines.flow.Flow

interface HealthArticleRepository {
    fun getHealthArticles(): Flow<List>
    suspend fun refreshArticles(): Result
}