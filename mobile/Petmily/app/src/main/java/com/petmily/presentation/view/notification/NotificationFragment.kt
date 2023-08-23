package com.petmily.presentation.view.notification

import android.os.Bundle
import android.system.Os.bind
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentNotificationBinding
import com.petmily.databinding.ItemNotificationBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.ResponseNotification

class NotificationFragment :
    BaseFragment<FragmentNotificationBinding>(FragmentNotificationBinding::bind, R.layout.fragment_notification) {

    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var notificationAdapter: NotificationAdapter

    private val boardViewModel: BoardViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviInVisible()
        initAdapter()
        initBtn()
        initObserve()
    }

    private fun initAdapter() = with(binding) {
        notificationAdapter = NotificationAdapter().apply {
            setNotificationListClickListener(object : NotificationAdapter.NotificationClickListener {
                override fun notificationClick(
                    binding: ItemNotificationBinding,
                    noti: ResponseNotification,
                    position: Int,
                ) {
                    boardViewModel.selectedBoard = Board(boardId = noti.boardId)
                    mainActivity.changeFragment("board detail")
                }
            })
        }
        rcvNotiList.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun initObserve() = with(mainViewModel) {
        resultNotification.observe(viewLifecycleOwner) {
            notificationAdapter.setNotis(it)
        }
    }

    private fun initBtn() = with(binding) {
        // 뒤로가기 버튼 클릭
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
