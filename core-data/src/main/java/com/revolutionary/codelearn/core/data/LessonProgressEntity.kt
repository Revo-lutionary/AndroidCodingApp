package com.revolutionary.codelearn.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val languageId: String,
    val status: String,
    val lastAttemptCode: String?,
    val completedAt: Long?,
)
