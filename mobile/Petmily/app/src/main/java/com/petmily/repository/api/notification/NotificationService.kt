package com.petmily.repository.api.notification

import android.util.Log
import com.petmily.repository.dto.FcmToken
import com.petmily.repository.dto.ResponseNotification
import com.petmily.util.RetrofitUtil

private const val TAG = "petmily_NotificationSer"
class NotificationService {

    /**
     * FCM 토근 저장
     */
    suspend fun requstSaveToken(fcmToken: FcmToken): String {
        return try {
            return RetrofitUtil.notificationApi.requstSaveToken(fcmToken)
        } catch (e: Exception) {
            Log.d(TAG, "requstSaveToken Exception: ${e.message}")
            throw Exception()
        }
    }

    /**
     * API - notification list
     */
    suspend fun requestNotification(userEmail: String): MutableList<ResponseNotification> {
        return try {
            return RetrofitUtil.notificationApi.requestNotification(userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "requestNotification Exception: ${e.message}")
            mutableListOf()
        }
    }
}
