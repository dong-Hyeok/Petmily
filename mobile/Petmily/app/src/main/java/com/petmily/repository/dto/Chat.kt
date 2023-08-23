package com.petmily.repository.dto

data class Chat(
    var id: Long = 0L,
    var writer: String = "", // 이메일
    var message: String = "",
    var createdAt: String = "", // 생성일
)
