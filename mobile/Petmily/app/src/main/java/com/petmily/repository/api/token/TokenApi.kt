package com.petmily.repository.api.token

import com.petmily.repository.dto.TokenRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {
    @POST("/refreshAccessToken")
    suspend fun refreshAccessToken(
        @Body body: TokenRequestDto,
    ): String
}
