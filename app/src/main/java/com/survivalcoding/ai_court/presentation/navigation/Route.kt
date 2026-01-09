package com.survivalcoding.ai_court.presentation.navigation

import android.net.Uri

sealed class Route(val route: String) {
    data object Entry : Route("entry")

    data object Waiting : Route("waiting/{inviteCode}/{chatRoomId}/{nickname}") {
        fun createRoute(inviteCode: String, chatRoomId: Long, nickname: String) =
            "waiting/$inviteCode/$chatRoomId/${android.net.Uri.encode(nickname)}"
    }

    data object Join : Route("join")

    data object Chat : Route("chat/{roomCode}/{nickname}") {
        fun createRoute(roomCode: String, nickname: String) =
            "chat/$roomCode/${Uri.encode(nickname)}"
    }

    data object Verdict : Route("verdict/{roomCode}") {
        fun createRoute(roomCode: String) = "verdict/$roomCode"
    }
}