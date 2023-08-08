package com.petmily.presentation.view.search

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentSearchBinding
import com.petmily.databinding.ItemBoardBinding
import com.petmily.databinding.ItemSearchUserBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.home.BoardAdapter
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.User

class SearchFragment :
    BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind, R.layout.fragment_search) {
    
    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }
    
    private lateinit var userAdapter: SearchUserAdapter
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var curationAdapter: SearchCurationAdapter
    
    private val mainViewModel: MainViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()
    
    // 피드 게시물 데이터 TODO: api 통신 후 적용되는 실제 데이터로 변경
    private val boards =
        listOf(
            Board(), Board(), Board(), Board(), Board(),
            Board(), Board(), Board(), Board(), Board(),
            Board(), Board(), Board(), Board(), Board(),
        )
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initBtn()
        initAdapter()
    }
    
    private fun initBtn() = with(binding) {
        // 검색 돋보기 버튼 클릭
        ivSearch.setOnClickListener {
        }
        
        // 사용자 카테고리 버튼 클릭
        btnCategoryUser.setOnClickListener {
            rcvSearch.adapter = userAdapter
        }
        
        // 피드 카테고리 버튼 클릭
        btnCategoryBoard.setOnClickListener {
            boardAdapter.setBoards(boards)
            rcvSearch.adapter = boardAdapter
        }
        
        // 큐레이션 카테고리 버튼 클릭
        btnCategoryCuration.setOnClickListener {
            rcvSearch.adapter = curationAdapter
        }
        
        // 뒤로가기 버튼 클릭
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    
    private fun initAdapter() = with(binding) {
        // 사용자 adapter
        userAdapter = SearchUserAdapter().apply {
            setUserClickListener(object : SearchUserAdapter.UserClickListener {
                override fun userClick(binding: ItemSearchUserBinding, user: User, position: Int) {
                    // TODO("Not yet implemented")
                }
            })
        }
        rcvSearch.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(mainActivity, LinearLayoutManager.VERTICAL))
        }
        
        // 피드 adapter
        boardAdapter = BoardAdapter(mainActivity).apply {
            setBoardClickListener(object : BoardAdapter.BoardClickListener {
                override fun heartClick(
                    isClicked: Boolean,
                    binding: ItemBoardBinding,
                    board: Board,
                    position: Int,
                ) {
                    if (isClicked) {
                        // 좋아요 등록
                        boardViewModel.registerHeart(board, mainViewModel)
                    } else {
                        // 좋아요 취소
                        boardViewModel.deleteHeart(board, mainViewModel)
                    }
                }
                override fun commentClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    // TODO("Not yet implemented")
                }
                override fun profileClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    // TODO("Not yet implemented")
                }
                override fun optionClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    // TODO("Not yet implemented")
                }
            })
        }
        
        // 큐레이션 adapter
        curationAdapter = SearchCurationAdapter().apply {
            // TODO("Not yet implemented")
        }
    }
}
