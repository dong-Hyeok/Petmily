package com.petmily.repository.api.certification.join

import android.util.Log
import com.petmily.repository.dto.EmailCode
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.util.RetrofitUtil

private const val TAG = "Fetmily_JoinService"
class JoinService {
    suspend fun requestEmailCode(userEmail: String): String {
        return try {
            RetrofitUtil.joinApi.requestEmailCode(EmailCode(userEmail))
        } catch (e: Exception) {
            Log.d(TAG, "sendEmailCode: ${e.message}")
            ""
        }
    }

    suspend fun checkEmailCode(code: String, userEmail: String): Boolean {
        return try {
            RetrofitUtil.joinApi.checkEmailCode(EmailCode(code, userEmail))
            true
        } catch (e: Exception) {
            Log.d(TAG, "checkEmailCode: ${e.message}")
            false
        }
    }

    suspend fun join(userEmail: String, userPw: String): Boolean {
        return try {
            RetrofitUtil.joinApi.signup(
                UserLoginInfoDto(
                    userEmail = userEmail,
                    userPw = userPw,
                ),
            )
            true
        } catch (e: Exception) {
            Log.d(TAG, "join: ${e.message}")
            false
        }
    }
}
