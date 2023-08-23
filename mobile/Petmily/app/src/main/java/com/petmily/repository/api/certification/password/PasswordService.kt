package com.petmily.repository.api.certification.password

import android.util.Log
import com.petmily.repository.dto.EmailCode
import com.petmily.util.RetrofitUtil

private const val TAG = "Petmily_PasswordService"

class PasswordService {
    suspend fun requestEmailCode(userEmail: String): String {
        return try {
            RetrofitUtil.passwordApi.requestEmailCode(EmailCode(userEmail))
        } catch (e: Exception) {
            Log.d(TAG, "requestEmailCode Exception: ${e.message}")
            ""
        }
    }

    suspend fun checkEmailCode(code: String, userEmail: String): Boolean {
        return try {
            RetrofitUtil.passwordApi.checkEmailCode(EmailCode(code, userEmail))
            true
        } catch (e: Exception) {
            Log.d(TAG, "checkEmailCode: ${e.message}")
            false
        }
    }

    suspend fun changePassRequest(userEmail: String): Boolean {
        return try {
            RetrofitUtil.passwordApi.requestChangePassword(EmailCode(userEmail))
            true
        } catch (e: Exception) {
            Log.d(TAG, "changePassRequest: ${e.message}")
            false
        }
    }
}
