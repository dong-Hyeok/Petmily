package com.petmily.repository.dto

data class MypageInfo(
    var userId: Long = 0L,
    var userEmail: String = "",
    var userNickname: String = "",
    var userProfileImg: String = "",
    var userRing: String? = "#ffffff",
    var userBadge: String? = "",
    var followingCount: Int = 0,
    var followerCount: Int = 0,
    var boardCount: Int = 0,
    var userPets: MutableList<Pet> = mutableListOf(),
)
