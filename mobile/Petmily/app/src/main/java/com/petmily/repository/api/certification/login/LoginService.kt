package com.petmily.repository.api.certification.login

import android.util.Log
import com.petmily.repository.dto.LoginResponse
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.util.RetrofitUtil

private const val TAG = "Fetmily_LoginService"
class LoginService {
    suspend fun login(email: String, pwd: String): LoginResponse {
        return try {
            RetrofitUtil.loginApi.login(
                UserLoginInfoDto(
                    userEmail = email,
                    userPw = pwd,
                ),
            )
        } catch (e: Exception) {
            Log.d(TAG, "login: ${e.message}")
            LoginResponse()
        }
    }
}
