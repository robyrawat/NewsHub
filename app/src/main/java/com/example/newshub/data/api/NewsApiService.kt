package com.example.newshub.data.api

import com.example.newshub.data.model.NewsResponse
import com.example.newshub.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * NewsApiService for NewsData.io API
 * Official documentation: https://newsdata.io/documentation/#latest_news
 */
interface NewsApiService {
    
    /**
     * Get latest news with proper NewsData.io parameters including country
     * NewsData.io requires both language and country for proper localization
     */
    @GET("news")
    suspend fun getLatestNews(
        @Query("apikey") apiKey: String = Constants.NEWS_API_KEY,
        @Query("language") language: String = "en",
        @Query("country") country: String = "in", // India for Hindi content
        @Query("size") size: Int = 10
    ): Response<NewsResponse>
    
    /**
     * Get news by category with proper parameters
     */
    @GET("news")
    suspend fun getNewsByCategory(
        @Query("category") category: String,
        @Query("apikey") apiKey: String = Constants.NEWS_API_KEY,
        @Query("language") language: String = "en",
        @Query("size") size: Int = 10
    ): Response<NewsResponse>
    
    /**
     * Search news articles
     */
    @GET("news")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("apikey") apiKey: String = Constants.NEWS_API_KEY,
        @Query("language") language: String = "en",
        @Query("size") size: Int = 10
    ): Response<NewsResponse>
    
    /**
     * Get trending/top news
     */
    @GET("news")
    suspend fun getTrendingNews(
        @Query("apikey") apiKey: String = Constants.NEWS_API_KEY,
        @Query("language") language: String = "en",
        @Query("category") category: String = "top",
        @Query("size") size: Int = 20
    ): Response<NewsResponse>

    /**
     * Get business news
     */
    @GET("news")
    suspend fun getBusinessNews(
        @Query("apikey") apiKey: String = Constants.NEWS_API_KEY,
        @Query("language") language: String = "en",
        @Query("category") category: String = "business",
        @Query("size") size: Int = 10
    ): Response<NewsResponse>
}
