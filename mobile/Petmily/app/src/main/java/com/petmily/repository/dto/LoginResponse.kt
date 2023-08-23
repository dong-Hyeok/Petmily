package com.petmily.repository.dto

data class LoginResponse(
    var result: Boolean = false,
    var message: String = "",
    var data: LoginData? = LoginData(),
)
