package com.petmily.repository.dto

data class LoginData(
    var accessToken: String = "",
    var userLoginInfoDto: UserLoginInfoDto? = UserLoginInfoDto(),
)
