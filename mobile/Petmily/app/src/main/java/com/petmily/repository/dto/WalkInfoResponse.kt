package com.petmily.repository.dto

data class WalkInfoResponse(
    var pet: Pet = Pet(),
    var walks: List<WalkInfo> = listOf(),
)
