package com.example.newshub.data.repository

import android.util.Log
import com.example.newshub.data.api.NewsApiService
import com.example.newshub.data.local.ArticleCache
import com.example.newshub.data.local.SampleNewsData
import com.example.newshub.data.model.Article
import com.example.newshub.data.preferences.LanguageManager
import com.example.newshub.data.preferences.UserPreferencesManager
import com.example.newshub.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling news data operations
 * Uses NewsData.io API with language support
 */
@Singleton
class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val articleCache: ArticleCache,
    private val preferencesManager: UserPreferencesManager
) {

    companion object {
        private const val TAG = "NewsRepository"
    }

    /**
     * Get latest news with language support and detailed logging
     */
    fun getLatestNews(): Flow<List<Article>> = flow {
        try {
            val uiLanguage = preferencesManager.language.first()
            val apiLanguage = LanguageManager.API_LANGUAGE_MAPPING[uiLanguage] ?: "en"

            // Set proper country for language - Hindi needs India, Arabic needs proper Arab countries, etc.
            val country = when (apiLanguage) {
                "hi" -> "in"  // India for Hindi
                "ar" -> "ae"  // UAE for Arabic
                "zh" -> "cn"  // China for Chinese
                "ja" -> "jp"  // Japan for Japanese
                "ko" -> "kr"  // Korea for Korean
                "ru" -> "ru"  // Russia for Russian
                "tr" -> "tr"  // Turkey for Turkish
                "nl" -> "nl"  // Netherlands for Dutch
                else -> "us"  // Default to US
            }

            Log.d(TAG, "Loading latest news - UI Language: $uiLanguage, API Language: $apiLanguage, Country: $country")
            Log.d(TAG, "API URL: ${Constants.NEWS_API_BASE_URL}news?apikey=${Constants.NEWS_API_KEY}&language=$apiLanguage&country=$country")

            val response = newsApiService.getLatestNews(language = apiLanguage, country = country)
            Log.d(TAG, "API Response - Success: ${response.isSuccessful}, Code: ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    val articles = newsResponse.results
                    Log.d(TAG, "Received ${articles.size} articles from API")

                    // Log sample article details to see language content
                    if (articles.isNotEmpty()) {
                        val firstArticle = articles.first()
                        Log.d(TAG, "First article language: ${firstArticle.language}")
                        Log.d(TAG, "First article title: ${firstArticle.title}")
                        Log.d(TAG, "First article source: ${firstArticle.source_name}")
                        Log.d(TAG, "First article country: ${firstArticle.country}")

                        // Log all article languages to see what we're getting
                        val languages = articles.map { it.language }.distinct()
                        Log.d(TAG, "All article languages received: $languages")

                        // Log all article IDs to debug article lookup issues
                        articles.forEach { article ->
                            Log.d(TAG, "Article ID: ${article.article_id}, Title: ${article.title}")
                        }
                    }

                    // Cache articles for offline reading
                    articleCache.saveArticles(articles)
                    Log.d(TAG, "Articles cached successfully")
                    emit(articles)
                } ?: run {
                    Log.w(TAG, "API response body is null, using sample data")
                    emit(SampleNewsData.getSampleArticles())
                }
            } else {
                Log.e(TAG, "API call failed with code: ${response.code()}")
                Log.e(TAG, "Error body: ${response.errorBody()?.string()}")
                Log.d(TAG, "Using sample data as fallback")
                emit(SampleNewsData.getSampleArticles())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getLatestNews", e)
            // Try to load cached articles as fallback
            val cachedArticles = articleCache.getCachedArticles()
            Log.d(TAG, "Loading ${cachedArticles.size} cached articles as fallback")
            if (cachedArticles.isNotEmpty()) {
                emit(cachedArticles)
            } else {
                // Fallback to sample data in case of error and no cached data
                Log.d(TAG, "Loading sample news data as final fallback")
                emit(SampleNewsData.getSampleArticles())
            }
        }
    }

    /**
     * Get news by category with language support
     */
    fun getNewsByCategory(category: String): Flow<List<Article>> = flow {
        try {
            val uiLanguage = preferencesManager.language.first()
            val apiLanguage = LanguageManager.API_LANGUAGE_MAPPING[uiLanguage] ?: "en"
            Log.d(
                TAG,
                "Loading category '$category' news - UI Language: $uiLanguage, API Language: $apiLanguage"
            )

            val response = newsApiService.getNewsByCategory(category, language = apiLanguage)
            Log.d(
                TAG,
                "Category API Response - Success: ${response.isSuccessful}, Code: ${response.code()}"
            )

            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    val articles = newsResponse.results
                    Log.d(TAG, "Received ${articles.size} articles for category '$category'")
                    emit(articles)
                } ?: run {
                    Log.w(TAG, "Category API response body is null")
                    emit(emptyList())
                }
            } else {
                Log.e(TAG, "Category API call failed with code: ${response.code()}")
                Log.e(TAG, "Error body: ${response.errorBody()?.string()}")
                handleApiError(response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getNewsByCategory for category '$category'", e)
            throw e
        }
    }

    /**
     * Search news articles with language support
     */
    fun searchNews(query: String): Flow<List<Article>> = flow {
        try {
            val language = preferencesManager.language.first()
            val response = newsApiService.searchNews(query, language = language)
            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    emit(newsResponse.results)
                } ?: emit(emptyList())
            } else {
                handleApiError(response.code())
            }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Get article by ID - for article details
     */
    fun getArticleById(articleId: String): Flow<Article?> = flow {
        try {
            Log.d(TAG, "Loading article by ID: $articleId")

            // Use the enhanced ArticleCache to find the article
            val article = articleCache.findArticleById(articleId)
            if (article != null) {
                Log.d(TAG, "Found article: ${article.title}")
                emit(article)
            } else {
                Log.w(TAG, "Article with ID $articleId not found in cache or bookmarks")
                emit(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getArticleById for ID: $articleId", e)
            emit(null)
        }
    }

    /**
     * Get trending news with language support
     */
    fun getTrendingNews(): Flow<List<Article>> = flow {
        try {
            val language = preferencesManager.language.first()
            val response = newsApiService.getTrendingNews(language = language)
            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    emit(newsResponse.results)
                } ?: emit(SampleNewsData.getSampleArticles()) // Fallback to sample data
            } else {
                Log.e(TAG, "Trending API call failed, using sample data")
                emit(SampleNewsData.getSampleArticles())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getTrendingNews, using sample data", e)
            emit(SampleNewsData.getSampleArticles())
        }
    }

    /**
     * Get business news with language support
     */
    fun getBusinessNews(): Flow<List<Article>> = flow {
        try {
            val language = preferencesManager.language.first()
            val response = newsApiService.getBusinessNews(language = language)
            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    emit(newsResponse.results)
                } ?: emit(SampleNewsData.getCategoryArticles("business"))
            } else {
                emit(SampleNewsData.getCategoryArticles("business"))
            }
        } catch (e: Exception) {
            emit(SampleNewsData.getCategoryArticles("business"))
        }
    }

    private fun handleApiError(code: Int) {
        val errorMessage = when (code) {
            401 -> "Invalid API key. Please check your NewsData.io API key."
            429 -> "Rate limit exceeded. Please try again later or update your API key."
            403 -> "Access forbidden. Please check your API subscription."
            500 -> "Server error. Please try again later."
            else -> "API Error: HTTP $code"
        }
        Log.e(TAG, "API Error: $errorMessage")
        // Don't throw exception for rate limits - just log and continue with fallback
        if (code != 429) {
            throw Exception(errorMessage)
        }
    }
}
