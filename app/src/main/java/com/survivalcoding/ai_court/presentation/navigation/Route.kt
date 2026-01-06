package com.survivalcoding.ai_court.presentation.navigation

sealed class Route(val route: String) {
    data object Entry : Route("entry")
    data object Chat : Route("chat/{roomCode}/{userId}/{nickname}") {
        fun createRoute(roomCode: String, userId: String, nickname: String): String {
            return "chat/$roomCode/$userId/$nickname"
        }
    }
}
