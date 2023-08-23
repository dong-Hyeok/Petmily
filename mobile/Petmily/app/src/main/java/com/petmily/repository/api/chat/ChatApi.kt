package com.petmily.repository.api.chat

import com.petmily.repository.dto.Chat
import com.petmily.repository.dto.ChatListResponse
import com.petmily.repository.dto.SenderReceiver
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {
    /**
     * API - 채팅방 생성
     */
    @POST("/chat/start")
    suspend fun createChatRoom(@Body body: SenderReceiver): String

    /**
     * API - 해당 채팅방 내용 GET
     */
    @POST("/chat/history")
    suspend fun requestChatData(@Body body: SenderReceiver): MutableList<Chat>

    /**
     * API - 채팅방 전체 목록 GET
     */
    @GET("/chat/{userEmail}")
    suspend fun requestChatList(
        @Path("userEmail") userEmail: String,
    ): MutableList<ChatListResponse>
}
