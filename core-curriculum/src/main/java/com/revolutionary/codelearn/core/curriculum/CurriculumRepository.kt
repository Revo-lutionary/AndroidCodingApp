package com.revolutionary.codelearn.core.curriculum

import android.content.Context
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.core.model.Lesson
import com.revolutionary.codelearn.core.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads lesson content from git-tracked JSON assets bundled in the APK,
 * under assets/curriculum. Lesson content is never stored in Room — only
 * mutable user progress is (see :core-data).
 */
class CurriculumRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadTracks(): List<Track> = withContext(Dispatchers.IO) {
        val manifest = json.decodeFromString<ManifestJson>(readAsset(MANIFEST_PATH))
        manifest.tracks.map { trackJson ->
            Track(
                id = trackJson.id,
                language = Language.fromId(trackJson.language),
                title = trackJson.title,
                lessonIds = trackJson.lessons,
            )
        }
    }

    suspend fun loadLesson(language: Language, trackId: String, lessonId: String): Lesson =
        withContext(Dispatchers.IO) {
            val path = "curriculum/${language.id}/$trackId/$lessonId.json"
            val lessonJson = json.decodeFromString<LessonJson>(readAsset(path))
            Lesson(
                id = lessonJson.id,
                language = language,
                trackId = trackId,
                title = lessonJson.title,
                explanationMarkdown = lessonJson.explanationMarkdown,
                starterCode = lessonJson.starterCode,
                solutionCode = lessonJson.solutionCode,
                hints = lessonJson.hints,
            )
        }

    private fun readAsset(path: String): String =
        context.assets.open(path).bufferedReader().use { it.readText() }

    private companion object {
        const val MANIFEST_PATH = "curriculum/manifest.json"
    }
}
