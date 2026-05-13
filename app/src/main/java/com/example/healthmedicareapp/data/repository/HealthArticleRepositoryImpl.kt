package com.example.healthmedicareapp.data.repository

import com.example.healthmedicareapp.BuildConfig
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

    override fun getHealthArticles(): Flow<List<HealthArticle>> {
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

    override suspend fun refreshArticles(): Result<Unit> {
        return try {
            val response = newsApiService.getHealthNews(apiKey = BuildConfig.NEWS_API_KEY)
            saveArticles(response.articles.map { article ->
                HealthArticleEntity(
                    url = article.url,
                    title = article.title ?: "",
                    description = article.description ?: "",
                    imageUrl = article.urlToImage,
                    publishedAt = article.publishedAt ?: "",
                    source = article.source?.name ?: ""
                )
            })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchArticles(query: String): Result<Unit> {
        return try {
            val response = newsApiService.searchHealthArticles(
                query = query,
                apiKey = BuildConfig.NEWS_API_KEY
            )
            saveArticles(response.articles.map { article ->
                HealthArticleEntity(
                    url = article.url,
                    title = article.title ?: "",
                    description = article.description ?: "",
                    imageUrl = article.urlToImage,
                    publishedAt = article.publishedAt ?: "",
                    source = article.source?.name ?: ""
                )
            })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveArticles(entities: List<HealthArticleEntity>) {
        val expiryTime = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
        healthArticleDao.deleteExpiredArticles(expiryTime)
        healthArticleDao.insertArticles(entities)
    }
}