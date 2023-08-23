package com.petmily.repository.api.token

import com.petmily.repository.dto.TokenRequestDto
import com.petmily.util.RetrofitUtil

class TokenService {
    /**
     * 액세스 토큰 재발급 요청
     */
    suspend fun refreshAccessToken(tokenRequestDto: TokenRequestDto): String {
        return try {
            RetrofitUtil.tokenApi.refreshAccessToken(tokenRequestDto)
        } catch (e: Exception) {
            // 토큰 재발급 실패
            ""
        }
    }
}
