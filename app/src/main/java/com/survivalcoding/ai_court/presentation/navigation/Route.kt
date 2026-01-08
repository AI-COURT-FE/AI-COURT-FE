package com.survivalcoding.ai_court.presentation.navigation

sealed class Route(val route: String) {
    data object Entry : Route("entry")

    data object Waiting : Route("waiting/{roomCode}/{chatRoomId}") {
        fun createRoute(roomCode: String, chatRoomId: Long) = "waiting/$roomCode/$chatRoomId"
    }

    data object Join : Route("join")

    data object Chat : Route("chat/{roomCode}/{chatRoomId}") {
        fun createRoute(roomCode: String, chatRoomId: Long) = "chat/$roomCode/$chatRoomId"
    }

    data object Verdict : Route("verdict/{roomCode}") {
        fun createRoute(roomCode: String) = "verdict/$roomCode"
    }
}