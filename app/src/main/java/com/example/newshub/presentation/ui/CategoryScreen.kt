package com.example.newshub.presentation.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavHostController
import com.example.newshub.presentation.ui.components.CommonTopBar
import com.example.newshub.presentation.viewmodel.NewsViewModel
import com.example.newshub.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val categoryNews by viewModel.categoryNews.collectAsState()
    val haptic = LocalHapticFeedback.current

    val categoryColors = remember {
        mapOf(
            "business" to Color(0xFF2196F3),
            "technology" to Color(0xFF9C27B0),
            "sports" to Color(0xFF4CAF50),
            "entertainment" to Color(0xFFFF5722),
            "health" to Color(0xFFE91E63),
            "science" to Color(0xFF607D8B),
            "politics" to Color(0xFF795548),
            "environment" to Color(0xFF8BC34A),
            "food" to Color(0xFFFF9800),
            "top" to Color(0xFFF44336),
            "world" to Color(0xFF3F51B5)
        )
    }

    val categoryIcons = remember {
        mapOf(
            "business" to Icons.Default.Business,
            "technology" to Icons.Default.Computer,
            "sports" to Icons.Default.SportsBaseball,
            "entertainment" to Icons.Default.Movie,
            "health" to Icons.Default.HealthAndSafety,
            "science" to Icons.Default.Science,
            "politics" to Icons.Default.AccountBalance,
            "environment" to Icons.Default.Eco,
            "food" to Icons.Default.Restaurant,
            "top" to Icons.Default.TrendingUp,
            "world" to Icons.Default.Public
        )
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Categories",
                navController = navController,
                showBackButton = false,
                actions = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Statistics",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Create rows of 2 categories each
            items(Constants.NEWS_CATEGORIES.chunked(2)) { categoryPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categoryPair.forEach { category ->
                        val displayName = Constants.CATEGORY_DISPLAY_NAMES[category] ?: category.replaceFirstChar { it.uppercase() }
                        val categoryColor = categoryColors[category] ?: Color(0xFF008577)
                        val categoryIcon = categoryIcons[category] ?: Icons.Default.Article
                        val articleCount = categoryNews[category]?.size ?: 0

                        CategoryCard(
                            title = displayName,
                            icon = categoryIcon,
                            color = categoryColor,
                            articleCount = articleCount,
                            onClick = {
                                navController.navigate("category_news/$category")
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fill remaining space if odd number of categories
                    if (categoryPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Add special cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clickable {
                                navController.navigate("trending")
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFF6B6B),
                                            Color(0xFFFF8E53)
                                        )
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Trending",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )

                                Text(
                                    text = "🔥 Trending",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clickable {
                                navController.navigate("bookmarks")
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF667eea),
                                            Color(0xFF764ba2)
                                        )
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdded,
                                    contentDescription = "Bookmarks",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )

                                Text(
                                    text = "📚 My Reads",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    articleCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )

                    if (articleCount > 0) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$articleCount",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = if (articleCount > 0) "$articleCount new articles" else "Tap to explore",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
