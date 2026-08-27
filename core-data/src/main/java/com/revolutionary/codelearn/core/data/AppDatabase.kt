package com.revolutionary.codelearn.core.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LessonProgressEntity::class, IdeTabEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonProgressDao(): LessonProgressDao
    abstract fun ideTabDao(): IdeTabDao

    companion object {
        const val DATABASE_NAME = "codelearn.db"
    }
}
