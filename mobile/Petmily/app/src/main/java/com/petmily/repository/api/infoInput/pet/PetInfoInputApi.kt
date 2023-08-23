package com.petmily.repository.api.infoInput.pet

import com.petmily.repository.dto.Pet
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface PetInfoInputApi {
    @Multipart
    @POST("/pet/save")
    suspend fun petSave(
        @Part file: MultipartBody.Part?,
        @Part("petInfoEditDto") petInfoEditDto: Pet,
    )

    @Multipart
    @POST("/pet/{petId}")
    suspend fun petUpdate(
        @Path("petId") petId: Long,
        @Part file: MultipartBody.Part?,
        @Part("petInfoEditDto") petInfoEditDto: Pet,
    )

    @DELETE("/pet/{petId}")
    suspend fun petDelete(
        @Path("petId") petId: Long,
    )
}
