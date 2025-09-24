package com.example.newshub.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.newshub.data.model.Article
import com.example.newshub.presentation.ui.components.CommonTopBar
import com.example.newshub.presentation.viewmodel.BookmarkViewModel
import com.example.newshub.presentation.viewmodel.NewsViewModel
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailsScreen(
    articleId: String,
    navController: NavHostController,
    newsViewModel: NewsViewModel = hiltViewModel(),
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    Log.d("ArticleDetailsScreen", "ArticleDetailsScreen composing with articleId: $articleId")

    val newsState by newsViewModel.newsState.collectAsState()
    val bookmarks by bookmarkViewModel.bookmarks.collectAsState()
    val context = LocalContext.current

    // Find the article by ID from news state or bookmarks
    val article = remember(articleId, newsState.articles, bookmarks) {
        Log.d("ArticleDetailsScreen", "Searching for article ID: $articleId")
        Log.d("ArticleDetailsScreen", "Available articles in news state: ${newsState.articles.size}")
        Log.d("ArticleDetailsScreen", "Available bookmarks: ${bookmarks.size}")

        // Log all available article IDs for debugging
        Log.d("ArticleDetailsScreen", "News state article IDs:")
        newsState.articles.forEach { article ->
            Log.d("ArticleDetailsScreen", "News ID: ${article.article_id}")
        }

        Log.d("ArticleDetailsScreen", "Bookmark article IDs:")
        bookmarks.forEach { article ->
            Log.d("ArticleDetailsScreen", "Bookmark ID: ${article.article_id}")
        }

        val foundInNews = newsState.articles.find { it.article_id == articleId }
        val foundInBookmarks = bookmarks.find { it.article_id == articleId }

        Log.d("ArticleDetailsScreen", "Found in news: ${foundInNews?.title ?: "null"}")
        Log.d("ArticleDetailsScreen", "Found in bookmarks: ${foundInBookmarks?.title ?: "null"}")

        foundInNews ?: foundInBookmarks
    }

    val isBookmarked = remember(articleId, bookmarks) {
        bookmarkViewModel.isBookmarked(articleId)
    }

    // Load article if not found (this handles direct navigation to article details)
    LaunchedEffect(articleId) {
        Log.d("ArticleDetailsScreen", "LaunchedEffect triggered for articleId: $articleId")
        if (article == null) {
            Log.d("ArticleDetailsScreen", "Article not found, calling loadArticleById")
            newsViewModel.loadArticleById(articleId)
        }
        // Add to reading history
        Log.d("ArticleDetailsScreen", "Adding to reading history: $articleId")
        bookmarkViewModel.addToReadingHistory(articleId)
    }

    Log.d("ArticleDetailsScreen", "Current article: ${article?.title ?: "null"}")
    Log.d("ArticleDetailsScreen", "Is loading: ${newsState.isLoading}")
    Log.d("ArticleDetailsScreen", "Error: ${newsState.error}")

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Article Details",
                navController = navController,
                showBackButton = true,
                actions = {
                    article?.let { currentArticle ->
                        // Bookmark button
                        IconButton(onClick = {
                            if (isBookmarked) {
                                bookmarkViewModel.removeBookmark(articleId)
                            } else {
                                bookmarkViewModel.addBookmark(currentArticle)
                            }
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Share button
                        IconButton(onClick = {
                            bookmarkViewModel.shareArticle(currentArticle, context)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Open in browser button
                        IconButton(onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(currentArticle.link))
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Open in browser",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            newsState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            article == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Article,
                            contentDescription = "No article",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Article not found",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            "The article might have been moved or deleted.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { navController.popBackStack() }
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }

            else -> {
                ArticleContent(
                    article = article,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ArticleContent(
    article: Article,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Article Image
        article.image_url?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "Article image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Category chips
        if (article.category.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(article.category) { category ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = category.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Title
        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            lineHeight = MaterialTheme.typography.headlineMedium.lineHeight
        )

        // Meta information
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Source",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = article.source_name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Published",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDate(article.pubDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Author
                article.creator?.firstOrNull()?.let { author ->
                    Divider()
                    Text(
                        text = "Author",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = author,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Description
        article.description?.let { description ->
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )
                }
            }
        }

        // Full Content
        article.content?.let { content ->
            if (content != article.description && content.isNotBlank()) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Full Article",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                        )
                    }
                }
            }
        }

        // Keywords
        article.keywords?.let { keywords ->
            if (keywords.isNotEmpty()) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Keywords",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 100.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(((keywords.size + 2) / 3) * 40.dp)
                        ) {
                            items(keywords) { keyword ->
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = keyword,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom spacer for better scrolling
        Spacer(modifier = Modifier.height(100.dp))
    }
}

private fun formatDate(pubDate: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault())
        val date = inputFormat.parse(pubDate)
        outputFormat.format(date ?: java.util.Date())
    } catch (e: Exception) {
        pubDate.take(10) // Fallback
    }
}
