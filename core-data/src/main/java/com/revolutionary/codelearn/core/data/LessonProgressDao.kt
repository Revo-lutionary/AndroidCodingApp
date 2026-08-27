package com.revolutionary.codelearn.core.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonProgressDao {
    @Query("SELECT * FROM lesson_progress WHERE languageId = :languageId")
    fun observeForLanguage(languageId: String): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    suspend fun getByLessonId(lessonId: String): LessonProgressEntity?

    @Upsert
    suspend fun upsert(entity: LessonProgressEntity)
}
