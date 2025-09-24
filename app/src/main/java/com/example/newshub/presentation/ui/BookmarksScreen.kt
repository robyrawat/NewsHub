package com.example.newshub.presentation.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.newshub.presentation.ui.components.CommonTopBar
import com.example.newshub.presentation.viewmodel.BookmarkSortType
import com.example.newshub.presentation.viewmodel.BookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    navController: NavHostController,
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarks by bookmarkViewModel.bookmarks.collectAsState()
    val isLoading by bookmarkViewModel.isLoading.collectAsState()
    val haptic = LocalHapticFeedback.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var articleToDelete by remember { mutableStateOf<String?>(null) }
    val pullToRefreshState = rememberPullToRefreshState()
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "My Bookmarks",
                navController = navController,
                showBackButton = false,
                actions = {
                    if (bookmarks.isNotEmpty()) {
                        IconButton(onClick = {
                            showSortDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        IconButton(onClick = {
                            showClearAllDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }) {
                            Icon(
                                imageVector = Icons.Default.ClearAll,
                                contentDescription = "Clear All",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (bookmarks.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("reading_list")
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = null) },
                    text = { Text("Reading List") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (bookmarks.isEmpty()) {
            EmptyBookmarksState(
                onExploreClick = {
                    navController.navigate("news")
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            PullToRefreshBox(
                isRefreshing = false,
                onRefresh = {
                    // TODO: Implement refresh logic
                    // bookmarkViewModel.refreshBookmarks()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        BookmarkStats(bookmarkCount = bookmarks.size)
                    }

                    items(bookmarks, key = { it.article_id }) { article ->
                        BookmarkCard(
                            article = article,
                            onArticleClick = {
                                navController.navigate("article_details/${article.article_id}")
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onRemoveClick = {
                                articleToDelete = article.article_id
                                showDeleteDialog = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onShareClick = {
                                bookmarkViewModel.shareArticle(article, navController.context)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete single bookmark dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                articleToDelete = null
            },
            title = { Text("Remove Bookmark") },
            text = { Text("Are you sure you want to remove this article from bookmarks?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        articleToDelete?.let { bookmarkViewModel.removeBookmark(it) }
                        showDeleteDialog = false
                        articleToDelete = null
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    articleToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear all bookmarks dialog
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Clear All Bookmarks") },
            text = { Text("Are you sure you want to remove all ${bookmarks.size} bookmarks? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        bookmarkViewModel.clearAllBookmarks()
                        showClearAllDialog = false
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Sort dialog
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("Sort Bookmarks") },
            text = {
                Column {
                    SortOption("Recently Added", BookmarkSortType.DATE_ADDED) {
                        bookmarkViewModel.sortBookmarks(it)
                        showSortDialog = false
                    }
                    SortOption("Title (A-Z)", BookmarkSortType.TITLE) {
                        bookmarkViewModel.sortBookmarks(it)
                        showSortDialog = false
                    }
                    SortOption("Source", BookmarkSortType.SOURCE) {
                        bookmarkViewModel.sortBookmarks(it)
                        showSortDialog = false
                    }
                    SortOption("Category", BookmarkSortType.CATEGORY) {
                        bookmarkViewModel.sortBookmarks(it)
                        showSortDialog = false
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SortOption(
    text: String,
    sortType: BookmarkSortType,
    onSelected: (BookmarkSortType) -> Unit
) {
    TextButton(
        onClick = { onSelected(sortType) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun EmptyBookmarksState(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated bookmark icon
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "No bookmarks",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "No Bookmarks Yet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Save articles to read them later. Your bookmarks will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onExploreClick,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Explore News")
                }
            }
        }
    }
}

@Composable
fun BookmarkStats(bookmarkCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📚 Your Reading List",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$bookmarkCount saved articles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "$bookmarkCount",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BookmarkCard(
    article: com.example.newshub.data.model.Article,
    onArticleClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onArticleClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row {
            // Image section
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(120.dp)
            ) {
                coil.compose.AsyncImage(
                    model = article.image_url ?: "https://via.placeholder.com/100x120?text=News",
                    contentDescription = "Article image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )

                // Category chip
                article.category.firstOrNull()?.let { category ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = category.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Content section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = article.source_name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Saved ${formatTimeAgo(article.pubDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onShareClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimeAgo(dateString: String): String {
    return try {
        // Simple time ago format
        "2 hours ago"
    } catch (e: Exception) {
        "Recently"
    }
}
