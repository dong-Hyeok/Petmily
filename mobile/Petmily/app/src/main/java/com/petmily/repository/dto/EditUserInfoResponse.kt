package com.petmily.repository.dto

data class EditUserInfoResponse(
    var userInfo: UserLoginInfoDto = UserLoginInfoDto(),
    var imageUrl: String? = "",
)
