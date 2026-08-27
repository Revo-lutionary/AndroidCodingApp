package com.revolutionary.codelearn.core.curriculum

import android.content.Context
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.core.model.Lesson
import com.revolutionary.codelearn.core.model.NodeType
import com.revolutionary.codelearn.core.model.QuizQuestion
import com.revolutionary.codelearn.core.model.RoadmapNode
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
                nodes = trackJson.nodes.map { nodeJson ->
                    RoadmapNode(id = nodeJson.id, type = NodeType.valueOf(nodeJson.type.uppercase()))
                },
            )
        }
    }

    suspend fun loadLesson(language: Language, trackId: String, nodeId: String): Lesson =
        withContext(Dispatchers.IO) {
            val lessonJson = json.decodeFromString<LessonJson>(readAsset(nodePath(language, trackId, nodeId)))
            Lesson(
                id = lessonJson.id,
                language = language,
                trackId = trackId,
                title = lessonJson.title,
                referenceMarkdown = lessonJson.referenceMarkdown,
                challengeMarkdown = lessonJson.challengeMarkdown,
                starterCode = lessonJson.starterCode,
                solutionCode = lessonJson.solutionCode,
                hints = lessonJson.hints,
            )
        }

    suspend fun loadQuiz(language: Language, trackId: String, nodeId: String): QuizQuestion =
        withContext(Dispatchers.IO) {
            val quizJson = json.decodeFromString<QuizJson>(readAsset(nodePath(language, trackId, nodeId)))
            QuizQuestion(
                id = quizJson.id,
                language = language,
                trackId = trackId,
                prompt = quizJson.prompt,
                choices = quizJson.choices,
                correctIndex = quizJson.correctIndex,
                explanation = quizJson.explanation,
            )
        }

    private fun nodePath(language: Language, trackId: String, nodeId: String) =
        "curriculum/${language.id}/$trackId/$nodeId.json"

    private fun readAsset(path: String): String =
        context.assets.open(path).bufferedReader().use { it.readText() }

    private companion object {
        const val MANIFEST_PATH = "curriculum/manifest.json"
    }
}
