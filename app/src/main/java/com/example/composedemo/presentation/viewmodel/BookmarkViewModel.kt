package com.example.composedemo.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composedemo.data.model.Article
import com.example.composedemo.data.local.ArticleCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val articleCache: ArticleCache
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<Article>>(emptyList())
    val bookmarks: StateFlow<List<Article>> = _bookmarks.asStateFlow()

    private val _bookmarkStats = MutableStateFlow(BookmarkStats())
    val bookmarkStats: StateFlow<BookmarkStats> = _bookmarkStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            _isLoading.value = true
            articleCache.getBookmarkedArticles()
                .collect { articles ->
                    _bookmarks.value = articles
                    updateStats(articles)
                    _isLoading.value = false
                }
        }
    }

    fun addBookmark(article: Article) {
        viewModelScope.launch {
            try {
                articleCache.bookmarkArticle(article)
            } catch (e: Exception) {
                // Handle error - could show a snackbar
                e.printStackTrace()
            }
        }
    }

    fun removeBookmark(articleId: String) {
        viewModelScope.launch {
            try {
                articleCache.removeBookmark(articleId)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    fun clearAllBookmarks() {
        viewModelScope.launch {
            try {
                articleCache.clearAllBookmarks()
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    fun isBookmarked(articleId: String): Boolean {
        return articleCache.isBookmarked(articleId)
    }

    fun shareArticle(article: Article, context: Context) {
        val shareText = "${article.title}\n\n${article.description ?: ""}\n\nRead more: ${article.link}"
        val intent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            putExtra(android.content.Intent.EXTRA_SUBJECT, article.title)
            type = "text/plain"
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Share Article"))
    }

    fun sortBookmarks(sortType: BookmarkSortType) {
        viewModelScope.launch {
            val currentBookmarks = _bookmarks.value
            val sortedBookmarks = when (sortType) {
                BookmarkSortType.DATE_ADDED -> currentBookmarks // Already sorted by most recent
                BookmarkSortType.TITLE -> currentBookmarks.sortedBy { it.title }
                BookmarkSortType.SOURCE -> currentBookmarks.sortedBy { it.source_name }
                BookmarkSortType.CATEGORY -> currentBookmarks.sortedBy { it.category.firstOrNull() ?: "" }
            }
            _bookmarks.value = sortedBookmarks
        }
    }

    private fun updateStats(articles: List<Article>) {
        val categoryCount = articles.groupBy { it.category.firstOrNull() ?: "general" }.size
        val totalReadingTime = articles.sumOf { (it.content?.length ?: it.description?.length ?: 0) / 200 + 1 }

        _bookmarkStats.value = BookmarkStats(
            totalArticles = articles.size,
            totalCategories = categoryCount,
            estimatedReadingTime = totalReadingTime
        )
    }

    fun addToReadingHistory(articleId: String) {
        viewModelScope.launch {
            try {
                articleCache.addToReadingHistory(articleId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class BookmarkStats(
    val totalArticles: Int = 0,
    val totalCategories: Int = 0,
    val estimatedReadingTime: Int = 0
)

enum class BookmarkSortType {
    DATE_ADDED,
    TITLE,
    SOURCE,
    CATEGORY
}
