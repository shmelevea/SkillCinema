package com.example.homework.di

import android.content.Context
import com.example.homework.data.ApiKeyProvider
import com.example.homework.data.remote.CinemaService
import com.example.homework.utils.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideCinemaService(retrofit: Retrofit): CinemaService {
        return retrofit.create(CinemaService::class.java)
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideApiKeyProvider(@ApplicationContext context: Context): ApiKeyProvider {
        return ApiKeyProvider(context)
    }

    @Provides
    fun provideOkHttpClient(apiKeyProvider: ApiKeyProvider): OkHttpClient {
        val apiKeyInterceptor = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header(NAME_KEY, apiKeyProvider.apiKey)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }
}
