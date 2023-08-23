package com.petmily.repository.dto

data class WalkInfo(
    var walkId: Long = 0L,
    var walkDate: String = "",
    var walkDistance: Int = 0,
    var walkSpend: Int = 0,
    var pet: Pet = Pet(),
)
