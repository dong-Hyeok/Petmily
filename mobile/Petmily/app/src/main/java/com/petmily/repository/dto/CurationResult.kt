package com.petmily.repository.dto

import com.google.gson.annotations.SerializedName

data class CurationResult(
    @SerializedName("강아지") var cDogList: MutableList<Curation> = mutableListOf(),
    @SerializedName("고양이") var cCatList: MutableList<Curation> = mutableListOf(),
    @SerializedName("기타동물") var cEtcList: MutableList<Curation> = mutableListOf(),
)
