package com.survivalcoding.ai_court.data.mapper

import com.survivalcoding.ai_court.data.dto.ChatMessageDto
import com.survivalcoding.ai_court.data.dto.VerdictDto
import com.survivalcoding.ai_court.data.dto.WinRateDto
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.model.Verdict
import com.survivalcoding.ai_court.domain.model.WinRate

fun ChatMessageDto.toDomain(currentUserId: String): ChatMessage {
    return ChatMessage(
        id = id,
        roomCode = roomCode,
        senderId = senderId,
        senderNickname = senderNickname,
        content = content,
        timestamp = timestamp,
        isMyMessage = senderId == currentUserId
    )
}

fun WinRateDto.toDomain(): WinRate {
    return WinRate(
        userAScore = userAScore,
        userBScore = userBScore
    )
}

fun VerdictDto.toDomain(): Verdict {
    return Verdict(
        winner = winner,
        winnerNickname = winnerNickname,
        scoreA = scoreA,
        scoreB = scoreB,
        reason = reason,
        summary = summary
    )
}

