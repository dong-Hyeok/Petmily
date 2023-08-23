package com.petmily.repository.dto

import com.google.gson.annotations.SerializedName

data class InventoryResult(
    @SerializedName("ring") var lingList: MutableList<Shop> = mutableListOf(),
    @SerializedName("badge") var badgeList: MutableList<Shop> = mutableListOf(),
    @SerializedName("background") var backgroundList: MutableList<Shop> = mutableListOf(),
)
