package com.petmily.repository.api.notification

import com.petmily.repository.dto.FcmToken
import com.petmily.repository.dto.ResponseNotification
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {

    /**
     * FCM 토근 저장
     */
    @POST("/fcm/token/save")
    suspend fun requstSaveToken(@Body body: FcmToken): String

    /**
     * API - notification list
     */
    @GET("/noti/{userEmail}")
    suspend fun requestNotification(
        @Path("userEmail") userEmail: String,
    ): MutableList<ResponseNotification>
}
