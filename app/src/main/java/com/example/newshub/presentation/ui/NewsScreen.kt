package com.example.newshub.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.newshub.presentation.ui.components.CommonTopBar
import com.example.newshub.presentation.ui.components.GridNewsCard
import com.example.newshub.presentation.ui.components.ImageOverlayNewsView
import com.example.newshub.presentation.ui.components.NewsCard
import com.example.newshub.presentation.viewmodel.NewsViewModel
import com.example.newshub.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay

enum class ViewMode {
    LIST_VERTICAL,
    LIST_HORIZONTAL,
    GRID,
    IMAGE_OVERLAY,
    CARD_STACK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    navController: NavHostController,
    newsViewModel: NewsViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val newsState by newsViewModel.newsState.collectAsState()
    val searchResults by newsViewModel.searchResults.collectAsState()
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val haptic = LocalHapticFeedback.current

    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Pull to refresh state
    var isRefreshing by remember { mutableStateOf(false) }

    // Handle pull to refresh
    val onRefresh = {
        isRefreshing = true
        newsViewModel.refresh()
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    // Reset refreshing state when loading completes
    LaunchedEffect(newsState.isLoading) {
        if (!newsState.isLoading && isRefreshing) {
            delay(500) // Small delay for better UX
            isRefreshing = false
        }
    }

    // Get view mode from settings
    val currentViewMode = remember(settingsState.viewMode) {
        try {
            ViewMode.valueOf(settingsState.viewMode)
        } catch (e: Exception) {
            ViewMode.LIST_VERTICAL
        }
    }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchAppBar(
                    query = searchQuery,
                    onQueryChanged = {
                        searchQuery = it
                        newsViewModel.searchNews(it)
                    },
                    onCloseClicked = {
                        showSearch = false
                        searchQuery = ""
                        newsViewModel.clearSearch()
                    }
                )
            } else {
                CommonTopBar(
                    title = "Latest News",
                    navController = navController,
                    showBackButton = false,
                    actions = {
                        // View Mode Selector
                        IconButton(onClick = {
                            val nextViewMode = when (currentViewMode) {
                                ViewMode.LIST_VERTICAL -> ViewMode.LIST_HORIZONTAL
                                ViewMode.LIST_HORIZONTAL -> ViewMode.GRID
                                ViewMode.GRID -> ViewMode.IMAGE_OVERLAY
                                ViewMode.IMAGE_OVERLAY -> ViewMode.CARD_STACK
                                ViewMode.CARD_STACK -> ViewMode.LIST_VERTICAL
                            }
                            settingsViewModel.updateViewMode(nextViewMode.name)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }) {
                            Icon(
                                imageVector = when (currentViewMode) {
                                    ViewMode.LIST_VERTICAL -> Icons.Default.ViewList
                                    ViewMode.LIST_HORIZONTAL -> Icons.Default.ViewCarousel
                                    ViewMode.GRID -> Icons.Default.GridView
                                    ViewMode.IMAGE_OVERLAY -> Icons.Default.ViewCompact
                                    ViewMode.CARD_STACK -> Icons.Default.ViewModule
                                },
                                contentDescription = "View Mode",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        IconButton(onClick = {
                            showSearch = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        // Removed refresh button from toolbar - using pull-to-refresh instead
                    }
                )
            }
        },
        floatingActionButton = {
            if (!showSearch && newsState.articles.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("trending")
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
                    text = { Text("Trending") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    ) { paddingValues ->
        val articlesToShow = if (searchQuery.isNotEmpty()) searchResults else newsState.articles

        when {
            newsState.isLoading && articlesToShow.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading latest news...")
                    }
                }
            }

            newsState.error != null && articlesToShow.isEmpty() -> {
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
                            text = "Failed to load news",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = newsState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { newsViewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            articlesToShow.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No articles found")
                }
            }

            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    when (currentViewMode) {
                        ViewMode.IMAGE_OVERLAY -> {
                            ImageOverlayNewsView(
                                articles = articlesToShow,
                                onArticleClick = { article ->
                                    navController.navigate("article_details/${article.article_id}")
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        ViewMode.GRID -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(articlesToShow) { article ->
                                    GridNewsCard(
                                        article = article,
                                        onArticleClick = {
                                            navController.navigate("article_details/${article.article_id}")
                                        }
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(articlesToShow, key = { it.article_id }) { article ->
                                    NewsCard(
                                        article = article,
                                        onArticleClick = {
                                            navController.navigate("article_details/${article.article_id}")
                                        },
                                        isHorizontal = currentViewMode == ViewMode.LIST_HORIZONTAL
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAppBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onCloseClicked: () -> Unit
) {
    TopAppBar(
        title = {
            // Ensure you are importing TextField from androidx.compose.material3.TextField
            androidx.compose.material3.TextField(
                value = query,
                onValueChange = onQueryChanged,
                placeholder = { Text("Search news...") },
                // Use TextFieldDefaults from androidx.compose.material3
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface, // Or another appropriate color
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface, // Or another appropriate color
                    disabledContainerColor = MaterialTheme.colorScheme.surface, // Or another appropriate color
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true // Assuming 'tr' was a typo for true
            )
        },
        // You might want to add navigationIcon for the close button
        navigationIcon = {
            IconButton(onClick = onCloseClicked) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close search"
                )
            }
        },
        // Optional: Add actions if needed
        actions = {
            // e.g., a clear text button
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear search query"
                    )
                }
            }
        }
    )
}