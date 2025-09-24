package com.example.newshub.data.local

import com.example.newshub.data.model.Article

object SampleNewsData {

    fun getSampleArticles(): List<Article> = listOf(
        Article(
            article_id = "sample_1",
            title = "Breaking: Major Technology Breakthrough Announced",
            link = "https://example.com/tech-breakthrough",
            description = "Scientists have made a groundbreaking discovery that could revolutionize the tech industry. This development promises to change how we interact with technology.",
            content = "In a major breakthrough that could reshape the technology landscape, researchers have announced a revolutionary discovery. The innovation promises to enhance user experience and improve efficiency across multiple platforms. Industry experts are calling this one of the most significant developments of the year.",
            pubDate = "2025-09-24 12:00:00",
            image_url = "https://picsum.photos/400/300?random=1",
            source_id = "tech_news",
            source_priority = 1,
            source_name = "Tech News Daily",
            source_url = "https://technews.com",
            source_icon = "https://technews.com/icon.png",
            language = "en",
            country = listOf("us"),
            category = listOf("technology"),
            keywords = listOf("technology", "breakthrough", "innovation", "science"),
            creator = listOf("John Doe"),
            ai_tag = "Technology",
            sentiment = "positive"
        ),
        Article(
            article_id = "sample_2",
            title = "Global Markets Show Strong Performance",
            link = "https://example.com/markets",
            description = "Stock markets worldwide are experiencing unprecedented growth as investors show confidence in emerging technologies and sustainable development initiatives.",
            content = "Financial markets around the globe are witnessing remarkable performance as investors increasingly favor companies focused on innovation and sustainability. This trend reflects a broader shift in investment strategies toward future-oriented businesses.",
            pubDate = "2025-09-24 11:30:00",
            image_url = "https://picsum.photos/400/300?random=2",
            source_id = "business_today",
            source_priority = 2,
            source_name = "Business Today",
            source_url = "https://businesstoday.com",
            source_icon = "https://businesstoday.com/icon.png",
            language = "en",
            country = listOf("us", "gb"),
            category = listOf("business"),
            keywords = listOf("markets", "stocks", "finance", "growth"),
            creator = listOf("Jane Smith"),
            ai_tag = "Business",
            sentiment = "positive"
        ),
        Article(
            article_id = "sample_3",
            title = "Climate Change Summit Reaches Historic Agreement",
            link = "https://example.com/climate-summit",
            description = "World leaders have reached a landmark agreement on climate action, setting ambitious targets for reducing carbon emissions and promoting renewable energy.",
            content = "In a historic moment for environmental policy, world leaders from over 190 countries have unanimously agreed to accelerate climate action. The comprehensive agreement includes binding commitments to reduce greenhouse gas emissions by 50% within the next decade.",
            pubDate = "2025-09-24 10:45:00",
            image_url = "https://picsum.photos/400/300?random=3",
            source_id = "world_news",
            source_priority = 1,
            source_name = "World News Network",
            source_url = "https://worldnews.com",
            source_icon = "https://worldnews.com/icon.png",
            language = "en",
            country = listOf("us", "gb", "ca"),
            category = listOf("environment", "politics"),
            keywords = listOf("climate", "environment", "summit", "agreement"),
            creator = listOf("Sarah Johnson"),
            ai_tag = "Environment",
            sentiment = "positive"
        ),
        Article(
            article_id = "sample_4",
            title = "Sports: Championship Finals Draw Record Audience",
            link = "https://example.com/sports-finals",
            description = "The championship finals attracted millions of viewers worldwide, breaking previous viewership records and showcasing exceptional athletic performance.",
            content = "Last night's championship finals captivated audiences around the world, with viewership numbers reaching an all-time high. The thrilling match showcased incredible skill and determination from both teams, making it one of the most memorable sporting events of the year.",
            pubDate = "2025-09-24 09:15:00",
            image_url = "https://picsum.photos/400/300?random=4",
            source_id = "sports_central",
            source_priority = 3,
            source_name = "Sports Central",
            source_url = "https://sportscentral.com",
            source_icon = "https://sportscentral.com/icon.png",
            language = "en",
            country = listOf("us"),
            category = listOf("sports"),
            keywords = listOf("sports", "championship", "finals", "record"),
            creator = listOf("Mike Wilson"),
            ai_tag = "Sports",
            sentiment = "positive"
        ),
        Article(
            article_id = "sample_5",
            title = "Health: New Research Shows Promise for Disease Treatment",
            link = "https://example.com/health-research",
            description = "Medical researchers have published groundbreaking findings that could lead to more effective treatments for a variety of diseases.",
            content = "A team of international researchers has published findings that represent a significant step forward in medical science. The research focuses on innovative treatment approaches that could benefit millions of patients worldwide.",
            pubDate = "2025-09-24 08:30:00",
            image_url = "https://picsum.photos/400/300?random=5",
            source_id = "health_today",
            source_priority = 2,
            source_name = "Health Today",
            source_url = "https://healthtoday.com",
            source_icon = "https://healthtoday.com/icon.png",
            language = "en",
            country = listOf("us", "gb"),
            category = listOf("health"),
            keywords = listOf("health", "research", "medical", "treatment"),
            creator = listOf("Dr. Emily Chen"),
            ai_tag = "Health",
            sentiment = "positive"
        ),
        Article(
            article_id = "sample_6",
            title = "Entertainment: Film Festival Celebrates Independent Cinema",
            link = "https://example.com/film-festival",
            description = "The annual film festival showcases the best of independent cinema, featuring diverse stories from emerging filmmakers around the world.",
            content = "This year's film festival has been particularly noteworthy for its emphasis on diverse storytelling and innovative cinematography. Independent filmmakers from around the globe have presented compelling narratives that challenge conventional thinking.",
            pubDate = "2025-09-24 07:45:00",
            image_url = "https://picsum.photos/400/300?random=6",
            source_id = "entertainment_weekly",
            source_priority = 4,
            source_name = "Entertainment Weekly",
            source_url = "https://ew.com",
            source_icon = "https://ew.com/icon.png",
            language = "en",
            country = listOf("us"),
            category = listOf("entertainment"),
            keywords = listOf("film", "festival", "cinema", "entertainment"),
            creator = listOf("Alex Rodriguez"),
            ai_tag = "Entertainment",
            sentiment = "positive"
        )
    )

    fun getCategoryArticles(category: String): List<Article> {
        return getSampleArticles().filter { article ->
            article.category.any { it.equals(category, ignoreCase = true) }
        }
    }

    fun searchArticles(query: String): List<Article> {
        return getSampleArticles().filter { article ->
            article.title.contains(query, ignoreCase = true) ||
            article.description?.contains(query, ignoreCase = true) == true ||
            article.keywords?.any { it.contains(query, ignoreCase = true) } == true
        }
    }
}
