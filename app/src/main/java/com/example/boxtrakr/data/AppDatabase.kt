package com.example.boxtrakr.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CategoryEntity::class, BoxEntity::class, BoxContentEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun boxDao(): BoxDao
    abstract fun boxContentDao(): BoxContentDao
}
