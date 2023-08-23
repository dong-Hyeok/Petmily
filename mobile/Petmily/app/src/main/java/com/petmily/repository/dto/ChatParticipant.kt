package com.petmily.repository.dto

data class ChatParticipant(
    var userId: Long = 0L,
    var userEmail: String = "",
    var userNickname: String = "",
    var userProfile: String = "",
    var userRing: String? = "#ffffff",
)

