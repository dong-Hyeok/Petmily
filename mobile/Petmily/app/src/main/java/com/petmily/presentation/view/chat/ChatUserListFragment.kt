package com.petmily.presentation.view.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentChatUserListBinding
import com.petmily.databinding.ItemChatUserListBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.ChatViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.repository.dto.ChatParticipant

class ChatUserListFragment :
    BaseFragment<FragmentChatUserListBinding>(FragmentChatUserListBinding::bind, R.layout.fragment_chat_user_list) {

    private val mainActivity by lazy {
        context as MainActivity
    }
    private val mainViewModel: MainViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()
    private lateinit var chatUserListAdapter: ChatUserListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initApi()
        initObserve()
        initAdapter()
        initBackPressEvent()
    }

    private fun initApi() = with(chatViewModel) {
        // 채팅방 리스트 요청
        requestChatList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserve() = with(chatViewModel) {
        // 채팅 내용
        resultChatContent.observe(viewLifecycleOwner) {
            mainActivity.changeFragment("chat detail")
        }

        // 채팅 목록
        resultChatList.observe(viewLifecycleOwner) {
            chatUserListAdapter.apply {
                submitChatList(it)
                notifyDataSetChanged()
            }
        }
    }

    private fun initBackPressEvent() {
        // 핸드폰 기기 back버튼
        mainActivity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainActivity.bottomNavigationView.selectedItemId = R.id.navigation_page_home
                }
            },
        )
    }

    private fun initAdapter() = with(binding) {
        chatUserListAdapter = ChatUserListAdapter().apply {
            // 채팅방 입장 클릭 이벤트
            setChatUserListClickListener(object : ChatUserListAdapter.ChatUserListClickListener {
                override fun chatUserListClick(
                    binding: ItemChatUserListBinding,
                    chatRoomId: String,
                    participant: ChatParticipant,
                    position: Int,
                ) {
                    // 채팅 룸 세팅
                    chatViewModel.setChattingRoomId(chatRoomId)

                    // 데이터 요청 participants[0]: 상태편
                    chatViewModel.requestChatData(participant.userEmail, mainViewModel)

                    // 현재 채팅방의 상대방 정보
                    chatViewModel.currentChatOther = participant
                }
            })
        }

        rcvChatUserList.apply {
            adapter = chatUserListAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() = with(chatViewModel) {
        initChatList()
        super.onDestroyView()
    }
}
