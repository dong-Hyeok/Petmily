package com.petmily.repository.dto

data class EmailCode(
    var code: String,
    var userEmail: String,
) {
    constructor(userEmail: String) : this("", userEmail)
}
