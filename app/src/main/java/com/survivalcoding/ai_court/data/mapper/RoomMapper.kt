package com.survivalcoding.ai_court.data.mapper

import com.survivalcoding.ai_court.data.dto.RoomDto
import com.survivalcoding.ai_court.domain.model.Room
import com.survivalcoding.ai_court.domain.model.User

fun RoomDto.toDomain(): Room {
    return Room(
        roomCode = roomCode,
        hostUser = User(
            sessionId = hostId,
            nickname = hostNickname
        ),
        guestUser = if (guestId != null && guestNickname != null) {
            User(
                sessionId = guestId,
                nickname = guestNickname
            )
        } else null,
        isReady = isReady
    )
}

