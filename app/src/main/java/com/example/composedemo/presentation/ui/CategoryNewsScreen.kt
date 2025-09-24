package com.example.composedemo.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.composedemo.presentation.ui.components.CommonTopBar
import com.example.composedemo.presentation.ui.components.NewsCard
import com.example.composedemo.presentation.viewmodel.NewsViewModel
import com.example.composedemo.util.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryNewsScreen(
    category: String,
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val categoryNews by viewModel.categoryNews.collectAsState()
    val isLoading by viewModel.newsState.collectAsState()
    val categoryDisplayName = Constants.CATEGORY_DISPLAY_NAMES[category] ?: category.capitalize()
    val haptic = LocalHapticFeedback.current

    // Pull to refresh state
    var isRefreshing by remember { mutableStateOf(false) }

    // Handle pull to refresh
    val onRefresh = {
        isRefreshing = true
        viewModel.loadNewsByCategory(category)
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    // Reset refreshing state when loading completes
    LaunchedEffect(isLoading.isLoading) {
        if (!isLoading.isLoading && isRefreshing) {
            delay(500) // Small delay for better UX
            isRefreshing = false
        }
    }

    LaunchedEffect(category) {
        viewModel.loadNewsByCategory(category)
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = categoryDisplayName,
                navController = navController,
                showBackButton = true
                // Removed refresh button from toolbar - using pull-to-refresh instead
            )
        }
    ) { paddingValues ->
        val articles = categoryNews[category] ?: emptyList()

        when {
            isLoading.isLoading && articles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading $categoryDisplayName news...")
                    }
                }
            }

            isLoading.error != null && articles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Failed to load $categoryDisplayName news",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = isLoading.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadNewsByCategory(category) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            articles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = "No articles",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No $categoryDisplayName articles found")
                    }
                }
            }

            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(articles, key = { it.article_id }) { article ->
                            NewsCard(
                                article = article,
                                onArticleClick = {
                                    navController.navigate("article_details/${article.article_id}")
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
