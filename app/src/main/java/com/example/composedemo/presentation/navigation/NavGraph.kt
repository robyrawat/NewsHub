package com.example.composedemo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.composedemo.presentation.ui.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("news") {
            NewsScreen(navController = navController)
        }
        composable("categories") {
            CategoryScreen(navController = navController)
        }
        composable("category_news/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CategoryNewsScreen(
                category = category,
                navController = navController
            )
        }
        composable("trending") {
            TrendingScreen(navController = navController)
        }
        composable("bookmarks") {
            BookmarksScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("article_details/{articleId}") { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailsScreen(
                articleId = articleId,
                navController = navController
            )
        }
        composable("search") {
            SearchScreen(navController = navController)
        }
        composable("reading_list") {
            ReadingListScreen(navController = navController)
        }
        composable("offline_articles") {
            OfflineArticlesScreen(navController = navController)
        }
    }
}
