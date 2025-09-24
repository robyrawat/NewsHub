package com.example.composedemo.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.composedemo.data.model.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.bookmarkDataStore: DataStore<Preferences> by preferencesDataStore(name = "bookmarks")
private val Context.articleCacheDataStore: DataStore<Preferences> by preferencesDataStore(name = "article_cache")

@Singleton
class ArticleCache @Inject constructor(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val _bookmarkedArticles = MutableStateFlow<List<Article>>(emptyList())
    private val _cachedArticles = MutableStateFlow<List<Article>>(emptyList())
    private val _readingHistory = MutableStateFlow<List<String>>(emptyList())

    companion object {
        private val BOOKMARKS_KEY = stringPreferencesKey("bookmarked_articles")
        private val READING_HISTORY_KEY = stringPreferencesKey("reading_history")
        private val CACHED_ARTICLES_KEY = stringPreferencesKey("cached_articles")
        private const val MAX_CACHED_ARTICLES = 200 // Keep up to 200 articles in cache
    }

    init {
        coroutineScope.launch {
            loadBookmarksFromStorage()
            loadReadingHistoryFromStorage()
            loadCachedArticlesFromStorage()

            // Initialize with sample data only if no cached articles exist at all
            if (_cachedArticles.value.isEmpty()) {
                _cachedArticles.value = com.example.composedemo.data.local.SampleNewsData.getSampleArticles()
                saveCachedArticlesToStorage()
                Log.d("ArticleCache", "Initialized with ${_cachedArticles.value.size} sample articles")
            }
        }
    }

    private suspend fun loadBookmarksFromStorage() {
        try {
            val bookmarksJson = context.bookmarkDataStore.data.first()[BOOKMARKS_KEY] ?: "[]"
            val articles = json.decodeFromString<List<Article>>(bookmarksJson)
            _bookmarkedArticles.value = articles
        } catch (e: Exception) {
            e.printStackTrace()
            _bookmarkedArticles.value = emptyList()
        }
    }

    private suspend fun saveBookmarksToStorage() {
        try {
            val bookmarksJson = json.encodeToString(_bookmarkedArticles.value)
            context.bookmarkDataStore.edit { preferences ->
                preferences[BOOKMARKS_KEY] = bookmarksJson
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadReadingHistoryFromStorage() {
        try {
            val historyJson = context.bookmarkDataStore.data.first()[READING_HISTORY_KEY] ?: "[]"
            val history = json.decodeFromString<List<String>>(historyJson)
            _readingHistory.value = history
        } catch (e: Exception) {
            e.printStackTrace()
            _readingHistory.value = emptyList()
        }
    }

    private suspend fun saveReadingHistoryToStorage() {
        try {
            val historyJson = json.encodeToString(_readingHistory.value)
            context.bookmarkDataStore.edit { preferences ->
                preferences[READING_HISTORY_KEY] = historyJson
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadCachedArticlesFromStorage() {
        try {
            val cachedArticlesJson = context.articleCacheDataStore.data.first()[CACHED_ARTICLES_KEY] ?: "[]"
            val articles = json.decodeFromString<List<Article>>(cachedArticlesJson)
            _cachedArticles.value = articles
        } catch (e: Exception) {
            e.printStackTrace()
            _cachedArticles.value = emptyList()
        }
    }

    private suspend fun saveCachedArticlesToStorage() {
        try {
            val cachedArticlesJson = json.encodeToString(_cachedArticles.value)
            context.articleCacheDataStore.edit { preferences ->
                preferences[CACHED_ARTICLES_KEY] = cachedArticlesJson
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get bookmarked articles as Flow
     */
    fun getBookmarkedArticles(): Flow<List<Article>> = _bookmarkedArticles.asStateFlow()

    /**
     * Get cached articles
     */
    fun getCachedArticles(): List<Article> = _cachedArticles.value

    /**
     * Cache articles for offline reading - merges with existing cache
     */
    suspend fun saveArticles(articles: List<Article>) {
        val currentCached = _cachedArticles.value.toMutableList()
        val existingIds = currentCached.map { it.article_id }.toSet()

        // Add only new articles that don't already exist
        val newArticles = articles.filter { it.article_id !in existingIds }
        currentCached.addAll(0, newArticles) // Add new articles to the beginning

        // Keep only the most recent articles if cache gets too large
        if (currentCached.size > MAX_CACHED_ARTICLES) {
            currentCached.subList(MAX_CACHED_ARTICLES, currentCached.size).clear()
        }

        _cachedArticles.value = currentCached
        saveCachedArticlesToStorage()

        Log.d("ArticleCache", "Added ${newArticles.size} new articles to cache. Total cached: ${currentCached.size}")
    }

    /**
     * Bookmark an article
     */
    suspend fun bookmarkArticle(article: Article) {
        val currentBookmarks = _bookmarkedArticles.value.toMutableList()
        if (!currentBookmarks.any { it.article_id == article.article_id }) {
            currentBookmarks.add(0, article) // Add to beginning
            _bookmarkedArticles.value = currentBookmarks
            saveBookmarksToStorage()
        }
    }

    /**
     * Remove bookmark
     */
    suspend fun removeBookmark(articleId: String) {
        val currentBookmarks = _bookmarkedArticles.value.toMutableList()
        val removed = currentBookmarks.removeAll { it.article_id == articleId }
        if (removed) {
            _bookmarkedArticles.value = currentBookmarks
            saveBookmarksToStorage()
        }
    }

    /**
     * Check if article is bookmarked
     */
    fun isBookmarked(articleId: String): Boolean {
        return _bookmarkedArticles.value.any { it.article_id == articleId }
    }

    /**
     * Clear all bookmarks
     */
    suspend fun clearAllBookmarks() {
        _bookmarkedArticles.value = emptyList()
        saveBookmarksToStorage()
    }

    /**
     * Add to reading history
     */
    suspend fun addToReadingHistory(articleId: String) {
        val currentHistory = _readingHistory.value.toMutableList()
        currentHistory.remove(articleId) // Remove if already exists
        currentHistory.add(0, articleId) // Add to beginning
        if (currentHistory.size > 100) { // Keep only last 100
            currentHistory.removeAt(currentHistory.size - 1)
        }
        _readingHistory.value = currentHistory
        saveReadingHistoryToStorage()
    }

    /**
     * Get reading history
     */
    fun getReadingHistory(): Flow<List<String>> = _readingHistory.asStateFlow()

    /**
     * Get recently read articles
     */
    fun getRecentlyReadArticles(): List<Article> {
        val history = _readingHistory.value
        val allArticles = _cachedArticles.value + _bookmarkedArticles.value
        return history.mapNotNull { articleId ->
            allArticles.find { it.article_id == articleId }
        }.take(20)
    }

    /**
     * Find a specific article by ID across all cached sources
     */
    fun findArticleById(articleId: String): Article? {
        Log.d("ArticleCache", "Searching for article ID: $articleId")

        // Search in cached articles first
        val cachedArticle = _cachedArticles.value.find { it.article_id == articleId }
        if (cachedArticle != null) {
            Log.d("ArticleCache", "Found article in cache: ${cachedArticle.title}")
            return cachedArticle
        }

        // Search in bookmarked articles
        val bookmarkedArticle = _bookmarkedArticles.value.find { it.article_id == articleId }
        if (bookmarkedArticle != null) {
            Log.d("ArticleCache", "Found article in bookmarks: ${bookmarkedArticle.title}")
            return bookmarkedArticle
        }

        Log.w("ArticleCache", "Article not found with ID: $articleId")
        Log.d("ArticleCache", "Total cached articles: ${_cachedArticles.value.size}")
        Log.d("ArticleCache", "Total bookmarked articles: ${_bookmarkedArticles.value.size}")

        return null
    }
}
