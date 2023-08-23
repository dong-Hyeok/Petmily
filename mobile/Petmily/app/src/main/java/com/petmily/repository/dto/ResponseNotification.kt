package com.petmily.repository.dto

data class ResponseNotification(
    var id: Long = 0L,
    var notiType: String = "", // FOLLOW, COMMENT, LIKE
    var fromUserEmail: String = "",
    var fromUserNickname: String = "",
    var fromUserProfileImg: String = "",
    var toUserEmail: String = "",
    var createDate: String = "",
    var checked: Boolean = false,
    var boardId: Long = 0L,
)
