package com.revolutionary.codelearn.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.revolutionary.codelearn.ui.screens.home.HomeScreen
import com.revolutionary.codelearn.ui.screens.lesson.LessonDetailScreen
import com.revolutionary.codelearn.ui.screens.lesson.LessonListScreen
import com.revolutionary.codelearn.ui.screens.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val LESSON_LIST = "lessons/{languageId}"
    const val LESSON_DETAIL = "lesson/{languageId}/{trackId}/{lessonId}"
    const val SETTINGS = "settings"

    fun lessonList(languageId: String) = "lessons/$languageId"
    fun lessonDetail(languageId: String, trackId: String, lessonId: String) =
        "lesson/$languageId/$trackId/$lessonId"
}

@Composable
fun CodeLearnNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onLanguageSelected = { language ->
                    navController.navigate(Routes.lessonList(language.id))
                },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.LESSON_LIST) { backStackEntry ->
            val languageId = backStackEntry.arguments?.getString("languageId").orEmpty()
            LessonListScreen(
                languageId = languageId,
                onLessonSelected = { trackId, lessonId ->
                    navController.navigate(Routes.lessonDetail(languageId, trackId, lessonId))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.LESSON_DETAIL) { backStackEntry ->
            val args = backStackEntry.arguments
            LessonDetailScreen(
                languageId = args?.getString("languageId").orEmpty(),
                trackId = args?.getString("trackId").orEmpty(),
                lessonId = args?.getString("lessonId").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
