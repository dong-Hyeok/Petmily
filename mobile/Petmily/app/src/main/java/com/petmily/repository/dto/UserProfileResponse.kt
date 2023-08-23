package com.petmily.repository.dto

data class UserProfileResponse(
    var userId: Long = 0L,
    var userEmail: String = "",
    var userNickname: String = "",
    var userProfileImg: String = "",
    var followingCurrentUser: Boolean = false,
)
