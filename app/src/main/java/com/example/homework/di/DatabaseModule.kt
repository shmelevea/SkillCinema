package com.example.homework.di

import android.content.Context
import androidx.room.Room
import com.example.homework.data.local.AppDatabase
import com.example.homework.data.local.AppDatabase.Companion.DATABASE_NAME
import com.example.homework.data.local.dao.CategoryDao
import com.example.homework.data.local.dao.MovieCategoryCrossRefDao
import com.example.homework.data.local.dao.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.movieDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideMovieCategoryCrossRefDao(appDatabase: AppDatabase): MovieCategoryCrossRefDao {
        return appDatabase.movieCategoryCrossRefDao()
    }
}