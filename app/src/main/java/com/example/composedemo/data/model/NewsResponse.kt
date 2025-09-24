package com.example.composedemo.data.model

import kotlinx.serialization.Serializable

/**
 * Data models for NewsData.io API
 */
@Serializable
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val results: List<Article>,
    val nextPage: String? = null
)

@Serializable
data class Article(
    val article_id: String,
    val title: String,
    val link: String,
    val keywords: List<String>? = null,
    val creator: List<String>? = null,
    val video_url: String? = null,
    val description: String? = null,
    val content: String? = null,
    val pubDate: String,
    val image_url: String? = null,
    val source_id: String,
    val source_priority: Int,
    val source_name: String,
    val source_url: String,
    val source_icon: String? = null,
    val language: String,
    val country: List<String>,
    val category: List<String>,
    val ai_tag: String? = null,
    val sentiment: String? = null,
    val sentiment_stats: String? = null,
    val ai_region: String? = null,
    val ai_org: String? = null,
    val duplicate: Boolean? = null
)
