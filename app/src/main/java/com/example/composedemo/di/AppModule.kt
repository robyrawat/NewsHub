package com.example.composedemo.di

import android.content.Context
import com.example.composedemo.data.api.NewsApiService
import com.example.composedemo.data.local.ArticleCache
import com.example.composedemo.data.preferences.LanguageManager
import com.example.composedemo.data.preferences.SettingsManager
import com.example.composedemo.data.preferences.UserPreferencesManager
import com.example.composedemo.data.repository.NewsRepository
import com.example.composedemo.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton // Ensures only one instance is created and reused
    fun provideUserPreferencesManager(
        @ApplicationContext context: Context
    ): UserPreferencesManager {
        // Here you create and return an instance of UserPreferencesManager
        // For example, if it needs a Context:
        return UserPreferencesManager(context)
        // If UserPreferencesManager has no dependencies in its constructor:
        // return UserPreferencesManager()
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.NEWS_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLanguageManager(
        @ApplicationContext context: Context,
        preferencesManager: UserPreferencesManager
    ): LanguageManager {
        return LanguageManager(context, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideArticleCache(@ApplicationContext context: Context): ArticleCache {
        return ArticleCache(context)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        apiService: NewsApiService,
        articleCache: ArticleCache,
        preferencesManager: UserPreferencesManager
    ): NewsRepository {
        return NewsRepository(apiService, articleCache, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideSettingsManager(
        @ApplicationContext context: Context,
        preferencesManager: UserPreferencesManager
    ): SettingsManager {
        return SettingsManager(context, preferencesManager)
    }
}
