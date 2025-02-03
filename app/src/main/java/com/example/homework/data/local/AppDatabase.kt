package com.example.homework.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.homework.data.local.dao.CategoryDao
import com.example.homework.data.local.dao.MovieCategoryCrossRefDao
import com.example.homework.data.local.dao.MovieDao
import com.example.homework.entity.CategoryEntity
import com.example.homework.entity.MovieCategoryCrossRef
import com.example.homework.entity.MovieEntity

@Database(
    entities = [MovieEntity::class, CategoryEntity::class, MovieCategoryCrossRef::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun categoryDao(): CategoryDao
    abstract fun movieCategoryCrossRefDao(): MovieCategoryCrossRefDao

    companion object {
        const val DATABASE_NAME = "skill_cinema_db"
    }
}