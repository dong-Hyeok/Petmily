package com.petmily.repository.api.walk

import com.petmily.repository.dto.WalkInfoResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WalkApi {
    @POST("/walk/save")
    suspend fun saveWalk(
        @Query("petId") petId: Long,
        @Query("walkDate") walkDate: String,
        @Query("walkDistance") walkDistance: Int,
        @Query("walkSpend") walkSpend: Int, // 산책 시간
    )

    @GET("/walk/getUserPetWalkInfo")
    suspend fun userPetWalkInfo(
        @Query("userEmail") userEmail: String,
    ): List<WalkInfoResponse>
}
