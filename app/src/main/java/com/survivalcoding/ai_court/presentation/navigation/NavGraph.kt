package com.survivalcoding.ai_court.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.survivalcoding.ai_court.presentation.chat.screen.ChatScreen
import com.survivalcoding.ai_court.presentation.entry.screen.EntryScreen

@Composable
fun CourtNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Entry.route,
        modifier = modifier
    ) {
        composable(route = Route.Entry.route) {
            EntryScreen(
                onNavigateToChat = { roomCode, userId, nickname ->
                    navController.navigate(Route.Chat.createRoute(roomCode, userId, nickname)) {
                        popUpTo(Route.Entry.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Route.Chat.route,
            arguments = listOf(
                navArgument("roomCode") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType },
                navArgument("nickname") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val nickname = backStackEntry.arguments?.getString("nickname") ?: ""

            ChatScreen(
                roomCode = roomCode,
                userId = userId,
                nickname = nickname,
                onNavigateBack = {
                    navController.navigate(Route.Entry.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
