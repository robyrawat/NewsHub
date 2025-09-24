package com.example.newshub.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newshub.data.model.Article
import com.example.newshub.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for News operations using NewsData.io API
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    companion object {
        private const val TAG = "NewsViewModel"
    }

    private val _newsState = MutableStateFlow(NewsUiState())
    val newsState: StateFlow<NewsUiState> = _newsState.asStateFlow()

    private val _categoryNews = MutableStateFlow<Map<String, List<Article>>>(emptyMap())
    val categoryNews: StateFlow<Map<String, List<Article>>> = _categoryNews.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Article>>(emptyList())
    val searchResults: StateFlow<List<Article>> = _searchResults.asStateFlow()

    init {
        Log.d(TAG, "NewsViewModel initialized")
        loadLatestNews()
    }

    /**
     * Load latest news from NewsData.io
     */
    fun loadLatestNews() {
        Log.d(TAG, "loadLatestNews called")
        viewModelScope.launch {
            _newsState.value = _newsState.value.copy(isLoading = true, error = null)
            Log.d(TAG, "Loading state set to true")

            newsRepository.getLatestNews()
                .catch { throwable ->
                    Log.e(TAG, "Error in loadLatestNews", throwable)
                    _newsState.value = _newsState.value.copy(
                        isLoading = false,
                        error = "Failed to load news: ${throwable.message}"
                    )
                }
                .collect { articles ->
                    Log.d(TAG, "Received ${articles.size} articles from repository")
                    _newsState.value = _newsState.value.copy(
                        articles = articles,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    /**
     * Load news by category
     */
    fun loadNewsByCategory(category: String) {
        Log.d(TAG, "loadNewsByCategory called with category: $category")
        viewModelScope.launch {
            newsRepository.getNewsByCategory(category)
                .catch { throwable ->
                    Log.e(TAG, "Error loading category $category", throwable)
                    // Handle category-specific errors
                    val currentMap = _categoryNews.value.toMutableMap()
                    currentMap[category] = emptyList()
                    _categoryNews.value = currentMap
                }
                .collect { articles ->
                    Log.d(TAG, "Loaded ${articles.size} articles for category: $category")
                    val currentMap = _categoryNews.value.toMutableMap()
                    currentMap[category] = articles
                    _categoryNews.value = currentMap
                }
        }
    }

    /**
     * Search news articles
     */
    fun searchNews(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            newsRepository.searchNews(query)
                .catch { throwable ->
                    _searchResults.value = emptyList()
                }
                .collect { articles ->
                    _searchResults.value = articles
                }
        }
    }

    /**
     * Refresh current news
     */
    fun refresh() {
        loadLatestNews()
    }

    /**
     * Load article by ID - for article details
     */
    fun loadArticleById(articleId: String) {
        Log.d(TAG, "loadArticleById called with ID: $articleId")
        viewModelScope.launch {
            // Try to find in current articles first
            val existingArticle = _newsState.value.articles.find { it.article_id == articleId }
            if (existingArticle != null) {
                Log.d(TAG, "Article found in current state: ${existingArticle.title}")
                return@launch
            }

            _newsState.value = _newsState.value.copy(isLoading = true)
            Log.d(TAG, "Article not found in current state, calling repository...")

            try {
                newsRepository.getArticleById(articleId)
                    .catch { throwable ->
                        Log.e(TAG, "Error loading article by ID", throwable)
                        _newsState.value = _newsState.value.copy(
                            isLoading = false,
                            error = "Failed to load article: ${throwable.message}"
                        )
                    }
                    .collect { article ->
                        Log.d(TAG, "Repository returned article: ${article?.title ?: "null"}")
                        if (article != null) {
                            // Add the article to current articles list
                            val updatedArticles = _newsState.value.articles.toMutableList()
                            updatedArticles.add(0, article)
                            _newsState.value = _newsState.value.copy(
                                articles = updatedArticles,
                                isLoading = false,
                                error = null
                            )
                            Log.d(TAG, "Article added to state. Total articles: ${_newsState.value.articles.size}")
                        } else {
                            _newsState.value = _newsState.value.copy(
                                isLoading = false,
                                error = "Article not found"
                            )
                            Log.w(TAG, "Article not found for ID: $articleId")
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in loadArticleById", e)
                _newsState.value = _newsState.value.copy(
                    isLoading = false,
                    error = "Failed to load article: ${e.message}"
                )
            }
        }
    }

    /**
     * Clear search results
     */
    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    /**
     * Get trending articles
     */
    fun loadTrendingNews() {
        viewModelScope.launch {
            newsRepository.getTrendingNews()
                .catch { throwable ->
                    // Handle trending news error
                }
                .collect { articles ->
                    // Update trending news if needed
                }
        }
    }
}

/**
 * UI State for News Screen
 */
data class NewsUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
