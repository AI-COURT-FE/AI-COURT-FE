package com.survivalcoding.ai_court.data.model.mapper

import com.survivalcoding.ai_court.data.model.response.ChatMessageDto
import com.survivalcoding.ai_court.domain.model.ChatMessage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object ChatMessageMapper {

    // 서버 예시: "2026-01-08T12:00:00"
    // 혹시 밀리초/타임존 붙는 경우까지 대비해서 여러 패턴 시도
    private val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    private fun parseCreatedAtToMillis(raw: String): Long {
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                    // 서버가 오프셋/Z를 안 주면 "로컬시간"으로 해석됨.
                    // (서버가 UTC로 준다면 TimeZone.getTimeZone("UTC")로 바꿔도 됨)
                    timeZone = TimeZone.getDefault()
                }
                val date = sdf.parse(raw) ?: continue
                return date.time
            } catch (_: ParseException) {
            } catch (_: IllegalArgumentException) {
            }
        }
        return 0L
    }

    /**
     * "내 메시지" 판별 우선순위:
     * 1) myUserId가 있으면 senderId로 비교 (추천)
     * 2) 없으면 nickname 비교 (차선: 중복 닉네임 가능)
     */
//    fun ChatMessageDto.toDomain(
//        myUserId: Long? = null,
//        myNickname: String? = null
//    ): ChatMessage {
//        val isMine = when {
//            myUserId != null -> senderId == myUserId
//            myNickname != null -> senderNickName == myNickname
//            else -> false
//        }
//
//        return ChatMessage(
//            id = messageId,
//            senderNickname = senderNickName,
//            content = content,
//            createdAt = parseCreatedAtToMillis(createdAt),
//            type = if (isMine) MessageType.ME else MessageType.OTHER
//        )
//    }
}