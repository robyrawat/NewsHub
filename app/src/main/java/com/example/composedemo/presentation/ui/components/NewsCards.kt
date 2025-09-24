package com.example.composedemo.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.composedemo.data.model.Article
import com.example.composedemo.presentation.viewmodel.BookmarkViewModel

@Composable
fun NewsCard(
    article: Article,
    onArticleClick: () -> Unit,
    isHorizontal: Boolean = false,
    modifier: Modifier = Modifier,
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarks by bookmarkViewModel.bookmarks.collectAsState()
    val isBookmarked by remember(article.article_id, bookmarks) {
        derivedStateOf { bookmarks.any { it.article_id == article.article_id } }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onArticleClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isHorizontal) {
            Row {
                NewsImageSection(
                    imageUrl = article.image_url,
                    category = article.category.firstOrNull(),
                    isBookmarked = isBookmarked,
                    onBookmarkClick = {
                        if (isBookmarked) {
                            bookmarkViewModel.removeBookmark(article.article_id)
                        } else {
                            bookmarkViewModel.addBookmark(article)
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                )

                NewsContentSection(
                    article = article,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = {
                        if (isBookmarked) {
                            bookmarkViewModel.removeBookmark(article.article_id)
                        } else {
                            bookmarkViewModel.addBookmark(article)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column {
                NewsImageSection(
                    imageUrl = article.image_url,
                    category = article.category.firstOrNull(),
                    isBookmarked = isBookmarked,
                    onBookmarkClick = {
                        if (isBookmarked) {
                            bookmarkViewModel.removeBookmark(article.article_id)
                        } else {
                            bookmarkViewModel.addBookmark(article)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                NewsContentSection(
                    article = article,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = {
                        if (isBookmarked) {
                            bookmarkViewModel.removeBookmark(article.article_id)
                        } else {
                            bookmarkViewModel.addBookmark(article)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GridNewsCard(
    article: Article,
    onArticleClick: () -> Unit,
    modifier: Modifier = Modifier,
    bookmarkViewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarks by bookmarkViewModel.bookmarks.collectAsState()
    val isBookmarked by remember(article.article_id, bookmarks) {
        derivedStateOf { bookmarks.any { it.article_id == article.article_id } }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onArticleClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(article.image_url ?: "https://via.placeholder.com/200x120?text=News")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )

                // Bookmark button with green color when bookmarked
                IconButton(
                    onClick = {
                        if (isBookmarked) {
                            bookmarkViewModel.removeBookmark(article.article_id)
                        } else {
                            bookmarkViewModel.addBookmark(article)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(16.dp)
                        )
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                        tint = if (isBookmarked) Color(0xFF4CAF50) else Color.White, // Green when bookmarked
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Category chip
                article.category.firstOrNull()?.let { category ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
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

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.source_name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun NewsImageSection(
    imageUrl: String?,
    category: String?,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl ?: "https://via.placeholder.com/300x200?text=News")
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Bookmark button with green color when bookmarked
        IconButton(
            onClick = onBookmarkClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Icon(
                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                tint = if (isBookmarked) Color(0xFF4CAF50) else Color.White // Green when bookmarked
            )
        }

        // Category chip
        category?.let {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = it.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NewsContentSection(
    article: Article,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (article.description != null) {
            Text(
                text = article.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = article.source_name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = formatPublishTime(article.pubDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = if (isBookmarked) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant // Green when bookmarked
                )
            }
        }
    }
}

private fun formatPublishTime(pubDate: String): String {
    return try {
        // Simple formatting - you can enhance this with proper date parsing
        val parts = pubDate.split(" ")
        if (parts.size >= 2) {
            "${parts[0]} ${parts[1]}"
        } else {
            pubDate.take(10)
        }
    } catch (e: Exception) {
        "Recently"
    }
}
