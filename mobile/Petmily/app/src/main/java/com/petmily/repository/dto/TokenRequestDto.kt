package com.petmily.repository.dto

data class TokenRequestDto(
    var refreshToken: String = "",
    var userEmail: String = "", 
)
