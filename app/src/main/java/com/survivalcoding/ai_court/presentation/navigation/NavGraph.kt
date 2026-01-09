package com.survivalcoding.ai_court.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.J
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
import com.survivalcoding.ai_court.presentation.entry.viewmodel.EntryViewModel
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
                onNavigateToWaiting = { inviteCode, chatRoomId, nickname ->
                    navController.navigate(Route.Waiting.createRoute(inviteCode, chatRoomId, nickname)) {
                        popUpTo(Route.Entry.route) { inclusive = true }
                    }
                },
                onNavigateToJoin = { navController.navigate(Route.Join.route) }
            )
        }

        composable(
            route = Route.Waiting.route,
            arguments = listOf(
                navArgument("inviteCode") { type = NavType.StringType },
                navArgument("chatRoomId") { type = NavType.LongType },
                navArgument("nickname") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inviteCode = backStackEntry.arguments?.getString("inviteCode").orEmpty()
            val chatRoomId = backStackEntry.arguments?.getLong("chatRoomId") ?: -1L
            val nickname = backStackEntry.arguments?.getString("nickname").orEmpty()

            WaitingScreen(
                inviteCode = inviteCode,
                chatRoomId = chatRoomId,
                onNavigateToChat = { id, code ->
                    navController.navigate(Route.Chat.createRoute(id.toString(), nickname)) {
                        popUpTo(Route.Waiting.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Route.Join.route) { backStackEntry ->

            val entryBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Route.Entry.route)
            }

            val entryViewModel: EntryViewModel = hiltViewModel(entryBackStackEntry)
            val entryState by entryViewModel.uiState.collectAsStateWithLifecycle()

            JoinScreen(
                nickname = entryState.nickname,
                onJoinSuccess = { roomCode ->
                    navController.navigate(Route.Chat.createRoute(roomCode, entryState.nickname)) {
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
                navArgument("nickname") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode").orEmpty()
            val nickname = backStackEntry.arguments?.getString("nickname").orEmpty()

            val generatedUserId = remember(roomCode) { java.util.UUID.randomUUID().toString() }

            ChatScreen(
                roomCode = roomCode,
                myUserId = generatedUserId,
                myNickname = nickname,           // 중요 (nickname= 말고 myNickname=)
                onNavigateBack = {
                    navController.navigate(Route.Entry.route) {
                        popUpTo(Route.Entry.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
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