package com.petmily.repository.dto

data class Pet(
    val petId: Long = 0L,
    var petName: String = "",
    var petGender: String = "",
    var petInfo: String = "",
    var petBirth: String = "",
    var petImg: String = "",
    val userEmail: String = "",
    val speciesName: String = "",
)
