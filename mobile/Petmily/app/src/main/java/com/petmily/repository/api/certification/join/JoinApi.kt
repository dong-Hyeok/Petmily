package com.petmily.repository.api.certification.join

import com.petmily.repository.dto.EmailCode
import com.petmily.repository.dto.UserLoginInfoDto
import retrofit2.http.Body
import retrofit2.http.POST

interface JoinApi {
    @POST("/signup/email")
    suspend fun requestEmailCode(@Body body: EmailCode): String

    @POST("/email/verification")
    suspend fun checkEmailCode(@Body body: EmailCode)

    @POST("/signup")
    suspend fun signup(@Body body: UserLoginInfoDto)
}
