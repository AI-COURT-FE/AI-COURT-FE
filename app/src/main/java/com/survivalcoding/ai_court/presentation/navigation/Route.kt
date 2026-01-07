package com.survivalcoding.ai_court.presentation.navigation

sealed class Route(val route: String) {
    data object Entry : Route("entry")

    data object Waiting : Route("waiting/{roomCode}") {
        fun createRoute(roomCode: String) = "waiting/$roomCode"
    }

    data object Join : Route("join")

    data object Chat : Route("chat/{roomCode}") {
        fun createRoute(roomCode: String) = "chat/$roomCode"
    }

    data object Verdict : Route("verdict/{roomCode}") {
        fun createRoute(roomCode: String) = "verdict/$roomCode"
    }
}