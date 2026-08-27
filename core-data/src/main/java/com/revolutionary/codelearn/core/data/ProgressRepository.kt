package com.revolutionary.codelearn.core.data

import com.revolutionary.codelearn.core.model.Language
import kotlinx.coroutines.flow.Flow

enum class LessonStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }

class ProgressRepository(private val dao: LessonProgressDao) {

    fun observeProgress(language: Language): Flow<List<LessonProgressEntity>> =
        dao.observeForLanguage(language.id)

    suspend fun markCompleted(lessonId: String, language: Language, code: String) {
        dao.upsert(
            LessonProgressEntity(
                lessonId = lessonId,
                languageId = language.id,
                status = LessonStatus.COMPLETED.name,
                lastAttemptCode = code,
                completedAt = System.currentTimeMillis(),
            )
        )
    }

    suspend fun statusFor(lessonId: String): LessonStatus {
        val entity = dao.getByLessonId(lessonId) ?: return LessonStatus.NOT_STARTED
        return LessonStatus.valueOf(entity.status)
    }
}
