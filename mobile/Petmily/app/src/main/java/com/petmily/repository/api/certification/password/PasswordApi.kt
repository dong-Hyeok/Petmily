package com.petmily.repository.api.certification.password

import com.petmily.repository.dto.EmailCode
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface PasswordApi {
    @POST("/resetpassword/email") // 이메일 코드 요청
    suspend fun requestEmailCode(@Body body: EmailCode): String

    @POST("/email/verification") // 이메일 코드 유효 체크
    suspend fun checkEmailCode(@Body body: EmailCode)

    @PUT("/resetpassword/reset") // 이메일 코드 체크 통과 후 확인버튼 누르면 -> 서버에 비밀번호 변경 요청
    suspend fun requestChangePassword(@Body body: EmailCode)
}
