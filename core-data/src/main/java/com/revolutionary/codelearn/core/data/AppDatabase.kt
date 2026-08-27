package com.revolutionary.codelearn.core.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LessonProgressEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonProgressDao(): LessonProgressDao

    companion object {
        const val DATABASE_NAME = "codelearn.db"
    }
}
