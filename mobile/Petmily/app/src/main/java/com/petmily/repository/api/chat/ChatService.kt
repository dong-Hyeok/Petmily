package com.petmily.repository.api.chat

import android.util.Log
import com.petmily.repository.dto.Chat
import com.petmily.repository.dto.ChatListResponse
import com.petmily.repository.dto.SenderReceiver
import com.petmily.util.RetrofitUtil

private const val TAG = "Permily_ChatService"
class ChatService {

    /**
     * API - 채팅방 생성
     */
    suspend fun createChatRoom(senderReceiver: SenderReceiver): String {
        return try {
            return RetrofitUtil.chatApi.createChatRoom(senderReceiver)
        } catch (e: Exception) {
            Log.d(TAG, "createChat Exception: ${e.message}")
            ""
        }
    }

    /**
     * API - 해당 채팅방 내용 GET
     */
    suspend fun requestChatData(senderReceiver: SenderReceiver): MutableList<Chat> {
        return try {
            RetrofitUtil.chatApi.requestChatData(senderReceiver)
        } catch (e: Exception) {
            Log.d(TAG, "requestChatData Exception: ${e.message}")
            mutableListOf()
        }
    }

    /**
     * API - 채팅방 전체 목록 GET
     */
    suspend fun requestChatList(userEmail: String): MutableList<ChatListResponse> {
        return try {
            return RetrofitUtil.chatApi.requestChatList(userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "requestChatList Exception: ${e.message}")
            mutableListOf()
        }
    }
}
