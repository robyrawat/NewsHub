package com.example.composedemo.util

/**
 * Application constants for NewsHub
 * Using NewsData.io API
 */
object Constants {

    // NewsData.io API Configuration
    const val NEWS_API_BASE_URL = "https://newsdata.io/api/1/"
    const val NEWS_API_KEY = "pub_c82b3f171be34fc0b6ea6eb477bc3779"

    // NewsData.io Endpoints
    const val ENDPOINT_NEWS = "news"
    const val ENDPOINT_ARCHIVE = "archive"
    const val ENDPOINT_SOURCES = "sources"

    // Default Parameters
    const val DEFAULT_LANGUAGE = "en"
    const val DEFAULT_COUNTRY = "us"
    const val DEFAULT_SIZE = 10
    const val MAX_SIZE = 50

    // Available Categories (NewsData.io format)
    val NEWS_CATEGORIES = listOf(
        "business",
        "entertainment",
        "environment",
        "food",
        "health",
        "politics",
        "science",
        "sports",
        "technology",
        "top",
        "world"
    )

    // Category Display Names
    val CATEGORY_DISPLAY_NAMES = mapOf(
        "business" to "Business",
        "entertainment" to "Entertainment",
        "environment" to "Environment",
        "food" to "Food",
        "health" to "Health",
        "politics" to "Politics",
        "science" to "Science",
        "sports" to "Sports",
        "technology" to "Technology",
        "top" to "Top Stories",
        "world" to "World"
    )

    // Available Countries
    val COUNTRIES = listOf("us", "gb", "ca", "au", "in")

    // Available Languages
    val LANGUAGES = listOf("en", "es", "fr", "de", "it", "pt")
}
