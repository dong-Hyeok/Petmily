package com.petmily.repository.dto

import androidx.lifecycle.MutableLiveData

data class Photo(
    var imgUrl: String = "",
    var isSelected: MutableLiveData<Boolean> = MutableLiveData(false),
)
