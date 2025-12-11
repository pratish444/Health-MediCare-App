package com.example.healthmedicareapp.data.repository

import com.example.healthmedicareapp.data.local.dao.HealthArticleDao
import com.example.healthmedicareapp.data.local.entities.HealthArticleEntity
import com.example.healthmedicareapp.data.remote.api.NewsApiService
import com.example.healthmedicareapp.domain.model.HealthArticle
import com.example.healthmedicareapp.domain.repository.HealthArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HealthArticleRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val healthArticleDao: HealthArticleDao
) : HealthArticleRepository {

    override fun getHealthArticles(): Flow<List> {
        return healthArticleDao.getAllArticles().map { entities ->
            entities.map { entity ->
                HealthArticle(
                    url = entity.url,
                    title = entity.title,
                    description = entity.description,
                    imageUrl = entity.imageUrl,
                    publishedAt = entity.publishedAt,
                    source = entity.source
                )
            }
        }
    }

    override suspend fun refreshArticles(): Result {
        return try {
            val response = newsApiService.searchHealthArticles(
                apiKey = BuildConfig.NEWS_API_KEY //importing this correctly ask from gpt or claoude
            )

            val entities = response.articles.map { article ->
                HealthArticleEntity(
                    url = article.url,
                    title = article.title,
                    description = article.description ?: "",
                    imageUrl = article.urlToImage,
                    publishedAt = article.publishedAt,
                    source = article.source.name
                )
            }

            // Delete old articles (older than 7 days)
            val expiryTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            healthArticleDao.deleteExpiredArticles(expiryTime)

            // Insert new articles
            healthArticleDao.insertArticles(entities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}