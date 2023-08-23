package com.petmily.repository.dto

data class ChatListResponse(
    var roomId: String = "",
    var participants: MutableList<ChatParticipant> = mutableListOf(),
    var latestMessage: String = "",
    var createdAt: String = "",
    var unreadMessageCount: Long = 0L,
)
