package com.petmily.repository.dto

data class Comment(
    var commentId: Long = 0L,
    var commentContent: String = "",
    var commentTime: String = "",
    var userEmail: String = "",
    var userNickname: String = "",
    var userProfileImg: String = "",
    var boardId: Long = 0L,
    var parentId: Long? = null, // null이여야 통신 성공함
)
