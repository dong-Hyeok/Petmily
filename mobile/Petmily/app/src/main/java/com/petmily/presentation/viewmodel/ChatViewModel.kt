package com.petmily.presentation.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.config.ApplicationClass
import com.petmily.repository.api.chat.ChatService
import com.petmily.repository.dto.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "petmily_ChatViewModel"
class ChatViewModel : ViewModel() {

    private val chatService: ChatService by lazy { ChatService() }

    // 상대방 mypage에서 메시지로 왔는지 판별 Email값 저장
    var fromChatDetailEmail = ""

    // 현재 채팅방의 상대방 정보
    var currentChatOther = ChatParticipant()

    // 채팅 룸 ID
    private var _resultChatRoomId = MutableLiveData<String>()
    val resultChatRoomId: LiveData<String>
        get() = _resultChatRoomId

    // 채팅 룸의 대화 내용
    private var _resultChatContent = MutableLiveData<MutableList<Chat>>()
    val resultChatContent: LiveData<MutableList<Chat>>
        get() = _resultChatContent

    // 채팅 목록
    private var _resultChatList = MutableLiveData<MutableList<ChatListResponse>>()
    val resultChatList: LiveData<MutableList<ChatListResponse>>
        get() = _resultChatList

    /**
     *  채팅 룸 id 지정
     */
    fun setChattingRoomId(roomId: String) {
        _resultChatRoomId.value = roomId
    }

    fun initChattingRoomId() { _resultChatRoomId = MutableLiveData<String>() }
    fun initChatContent() { _resultChatContent = MutableLiveData<MutableList<Chat>>() }
    fun initChatList() { _resultChatList = MutableLiveData<MutableList<ChatListResponse>>() }

    /**
     * API - 채팅방 생성
     */
    fun createChatRoom(otherEmail: String, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")
            _resultChatRoomId.value = chatService.createChatRoom(
                SenderReceiver(
                    sender = userEmail!!,
                    receiver = otherEmail,
                ),
            )

            Log.d(TAG, "createChatRoom: ${_resultChatRoomId.value}")
        }
    }

    /**
     * API - 해당 채팅방 내용 GET
     */
    fun requestChatData(otherEmail: String, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")

            _resultChatContent.value = chatService.requestChatData(
                SenderReceiver(
                    sender = userEmail!!,
                    receiver = otherEmail,
                ),
            )

            Log.d(TAG, "requestChatData: ${_resultChatContent.value}")
        }
    }

    /**
     * API - 채팅방 전체 목록 GET
     */
    fun requestChatList() {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")
            _resultChatList.value = chatService.requestChatList(userEmail!!)
            Log.d(TAG, "requestChatList $userEmail => : ${_resultChatList.value}")
        }
    }

    /*
    -------------------------------------------------------------------------
                                STOMP 통신
    -------------------------------------------------------------------------
     */

    private val url = "ws://i9d209.p.ssafy.io:8081/chatting/websocket"
    private val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

    @SuppressLint("CheckResult")
    fun runStomp() {
        /**
         * 메시지 수신 (콜백)
         * 전달되면 해당 람다 표현식 내의 코드가 자동 실행
         */
        stompClient.topic("/sub/room/${_resultChatRoomId.value}").subscribe { topicMessage ->
            Log.d("message Recieve", topicMessage.payload)
            val jsonPayload = topicMessage.payload
            val jsonObject = JSONObject(jsonPayload)

            val writer = jsonObject.getString("writer")
            val message = jsonObject.getString("message")
            Log.d(TAG, "runStomp: $writer / $message")

            /**
             * 이 부분 중요!!
             * 다른 배열에 내용을 담은 후 새로운 메시지 내용을 그 곳에 추가
             * 그리고 새로운 내용까지 포함한 데이터를 postValue로 변경&추가 되었음을 알림
             * -> 라이브데이타가 인식하고 동작
             */
            val chatContent = _resultChatContent.value
            var currentTime = currentTimeFormat()
            chatContent?.add(
                Chat(
                    writer = writer,
                    message = message,
                    createdAt = currentTime,
                ),
            )
            _resultChatContent.postValue(chatContent!!)
        }

        // 연결
        stompClient.connect()

        stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("OPENED", "opened")
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("CLOSED", "closed")
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.d("ERROR", "error")
                    Log.i("CONNECT ERROR", lifecycleEvent.exception.toString())
                }
                else -> {
                    Log.d("else", lifecycleEvent.message)
                }
            }
        }
    }

    fun disconnectStomp() {
        Log.d(TAG, "disconnectStomp: stomp 연결 종료")
        stompClient.disconnect()
    }

    @SuppressLint("CheckResult")
    fun sendStomp(message: String) {
        val writer = ApplicationClass.sharedPreferences.getString("userEmail")!!

        val data = JSONObject()
        data.put("roomId", "${_resultChatRoomId.value}")
        data.put("writer", writer)
        data.put("message", message)

        // 메시지를 보낼 엔드포인트 URL
        val messageSendEndpoint = "/pub/message"

        // stompClient.send() 함수로 메시지를 전송
        stompClient.send(messageSendEndpoint, data.toString()).subscribe(
            {
                // 성공시
                Log.d("Message Sent", "Message sent successfully")
            },
            { error ->
                // 실패시
                Log.d("Message Send Error", error.toString())
            },
        )
    }

    // 현재 시간을 변환 - 서버에서 오는 시간 형식으로 변환
    fun currentTimeFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val currentTime = Date()
        return try {
            dateFormat.format(currentTime)
        } catch (e: Exception) {
            "Invalid date and time format"
        }
    }
}
