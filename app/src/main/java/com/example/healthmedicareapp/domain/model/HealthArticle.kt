package com.example.healthmedicareapp.domain.model

data class HealthArticle(
    val url: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val publishedAt: String,
    val source: String
)