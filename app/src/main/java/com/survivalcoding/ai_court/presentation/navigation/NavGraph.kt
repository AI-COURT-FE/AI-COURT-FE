package com.survivalcoding.ai_court.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.survivalcoding.ai_court.presentation.chat.screen.ChatScreen
import com.survivalcoding.ai_court.presentation.chat.viewmodel.ChatViewModel
import com.survivalcoding.ai_court.presentation.entry.screen.EntryScreen
import com.survivalcoding.ai_court.presentation.join.screen.JoinScreen
import com.survivalcoding.ai_court.presentation.verdict.screen.VerdictScreen
import com.survivalcoding.ai_court.presentation.waiting.screen.WaitingScreen

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
                onNavigateToWaiting = { roomCode, userId, nickname ->
                    navController.navigate(Route.Waiting.createRoute(roomCode)) {
                        popUpTo(Route.Entry.route) { inclusive = true }
                    }
                },
                onNavigateToJoin = {
                    navController.navigate(Route.Join.route)
                }
            )
        }

        composable(route = Route.Join.route) {
            JoinScreen(
                onJoinSuccess = { roomCode ->
                    navController.navigate(Route.Chat.createRoute(roomCode)) {
                        popUpTo(Route.Join.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.Chat.route,
            arguments = listOf(
                navArgument("roomCode") { type = NavType.StringType },
//                navArgument("userId") { type = NavType.StringType },
//                navArgument("nickname") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
//            val userId = backStackEntry.arguments?.getString("userId") ?: ""
//            val nickname = backStackEntry.arguments?.getString("nickname") ?: ""
            val userId = ""
            val nickname = ""

            ChatScreen(
                roomCode = roomCode,
                myUserId = userId,
                onNavigateBack = {
                    navController.navigate(Route.Entry.route) {
                        popUpTo(Route.Entry.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Route.Waiting.route,
            arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode").orEmpty()

            WaitingScreen(
                roomCode = roomCode,
                onNavigateToChat = {
                    navController.navigate(Route.Chat.createRoute(roomCode)) {
                        popUpTo(Route.Waiting.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.Verdict.route,
            arguments = listOf(navArgument("roomCode") { type = NavType.StringType },)
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode").orEmpty()
            val chatViewModel: ChatViewModel = hiltViewModel()
            val chatState by chatViewModel.uiState.collectAsStateWithLifecycle()

            VerdictScreen(
                roomCode = roomCode,
                leftName = chatState.myNickname,
                rightName = chatState.opponentNickname ?: "상대",
                onNavigateBack = { navController.popBackStack() },
                onShareVerdict = {},
                onGoEntry = {
                    navController.navigate(Route.Entry.route) {
                        popUpTo(Route.Entry.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}