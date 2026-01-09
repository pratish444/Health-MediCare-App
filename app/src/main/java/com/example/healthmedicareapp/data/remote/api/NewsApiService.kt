package com.example.healthmedicareapp.data.remote.api


import com.example.healthmedicareapp.data.remote.dto.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getHealthNews(
        @Query("category") category: String = "health",
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String
    ): NewsResponse

    @GET("v2/everything")
    suspend fun searchHealthArticles(
        @Query("q") query: String = "health OR medicine OR wellness",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponse
}