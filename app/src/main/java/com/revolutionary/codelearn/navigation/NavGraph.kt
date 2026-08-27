package com.revolutionary.codelearn.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.revolutionary.codelearn.core.model.NodeType
import com.revolutionary.codelearn.ui.screens.lesson.LessonScreen
import com.revolutionary.codelearn.ui.screens.onboarding.LanguagePickerScreen
import com.revolutionary.codelearn.ui.screens.quiz.QuizScreen
import com.revolutionary.codelearn.ui.screens.roadmap.RoadmapScreen
import com.revolutionary.codelearn.ui.screens.settings.SettingsScreen

object Routes {
    const val LANGUAGE_PICKER = "languagePicker"
    const val ROADMAP = "roadmap/{languageId}"
    const val LESSON = "lesson/{languageId}/{trackId}/{nodeId}"
    const val QUIZ = "quiz/{languageId}/{trackId}/{nodeId}"
    const val SETTINGS = "settings"

    fun roadmap(languageId: String) = "roadmap/$languageId"
    fun lesson(languageId: String, trackId: String, nodeId: String) = "lesson/$languageId/$trackId/$nodeId"
    fun quiz(languageId: String, trackId: String, nodeId: String) = "quiz/$languageId/$trackId/$nodeId"
}

@Composable
fun CodeLearnNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LANGUAGE_PICKER) {
        composable(Routes.LANGUAGE_PICKER) {
            LanguagePickerScreen(
                onLanguageSelected = { language ->
                    navController.navigate(Routes.roadmap(language.id))
                },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.ROADMAP) { backStackEntry ->
            val languageId = backStackEntry.arguments?.getString("languageId").orEmpty()
            RoadmapScreen(
                languageId = languageId,
                onNodeSelected = { trackId, node ->
                    val route = when (node.type) {
                        NodeType.LESSON -> Routes.lesson(languageId, trackId, node.id)
                        NodeType.QUIZ -> Routes.quiz(languageId, trackId, node.id)
                    }
                    navController.navigate(route)
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.LESSON) { backStackEntry ->
            val args = backStackEntry.arguments
            LessonScreen(
                languageId = args?.getString("languageId").orEmpty(),
                trackId = args?.getString("trackId").orEmpty(),
                lessonId = args?.getString("nodeId").orEmpty(),
                onFinish = { navController.popBackStack() },
            )
        }
        composable(Routes.QUIZ) { backStackEntry ->
            val args = backStackEntry.arguments
            QuizScreen(
                languageId = args?.getString("languageId").orEmpty(),
                trackId = args?.getString("trackId").orEmpty(),
                quizId = args?.getString("nodeId").orEmpty(),
                onFinish = { navController.popBackStack() },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
