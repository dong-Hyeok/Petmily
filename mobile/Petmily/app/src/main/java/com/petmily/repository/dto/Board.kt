package com.petmily.repository.dto

data class Board(
    var boardId: Long = 0L,
    var boardContent: String = "",
    var boardUploadTime: String = "",
    var heartCount: Int = 0,
    var userEmail: String = "",
    var userProfileImageUrl: String = "",
    var userNickname: String = "",
    var userRing: String? = "#ffffff",
    var photoUrls: List<String> = listOf(),
    var hashTags: List<String> = listOf(),
    var comments: List<Comment> = listOf(),
    var likedByCurrentUser: Boolean = false,
)
