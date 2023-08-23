package com.petmily.repository.dto

import java.sql.Date

data class UserLoginInfoDto(
    var userId: Long = 0L,
    var userEmail: String = "",
    var userPw: String = "",
    var userToken: String = "",
    var userNickname: String = "",
    var userRegion: String = "",
    var userProfileImg: String? = "",
    var userLikePet: String = "",
    var userPoint: Int = 0,
    var userLoginDate: Date = Date(System.currentTimeMillis()),
    var userIsSocial: Boolean = false,
    var role: String = "",
)
