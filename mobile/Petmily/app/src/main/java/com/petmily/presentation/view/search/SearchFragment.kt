package com.petmily.presentation.view.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentSearchBinding
import com.petmily.databinding.ItemSearchBoardBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.dialog.CommentDialog
import com.petmily.presentation.view.dialog.OptionDialog
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.UserLoginInfoDto

class SearchFragment :
    BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind, R.layout.fragment_search) {

    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }

    private lateinit var searchBoardAdapter: SearchBoardAdapter

    private val mainViewModel: MainViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val commentDialog by lazy { CommentDialog(mainActivity, mainViewModel, boardViewModel) }
    private val optionDialog by lazy { OptionDialog(mainActivity, mainViewModel, boardViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviInVisible()

        initObserver()
        initBtn()
        initAdapter()
    }

    private fun initBtn() = with(binding) {
        // 검색 돋보기 버튼 클릭
        ivSearch.setOnClickListener {
            if (etSearch.text.isBlank()) {
                mainActivity.showSnackbar("검색어를 입력해주세요.")
                return@setOnClickListener
            }
            boardViewModel.searchBoard(
                etSearch.text.toString(),
                ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
            )
        }

        // 뒤로가기 버튼 클릭
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initAdapter() = with(binding) {
        // 피드 adapter
        searchBoardAdapter = SearchBoardAdapter(mainActivity).apply {
            setBoardClickListener(object : SearchBoardAdapter.BoardClickListener {
                override fun profileClick(binding: ItemSearchBoardBinding, board: Board, position: Int) {
                    userViewModel.selectedUserLoginInfoDto = UserLoginInfoDto(userEmail = board.userEmail)
                    mainActivity.changeFragment("my page")
                }
            })
        }
        rcvSearch.apply {
            adapter = searchBoardAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun initObserver() = with(boardViewModel) {
        // 내 피드 삭제
        initIsBoardDeleted()
        isBoardDeleted.observe(viewLifecycleOwner) {
            if (!it) {
                // 피드 삭제 실패
                mainActivity.showSnackbar("게시물 삭제에 실패하였습니다.")
            } else {
                // 피드 삭제 성공
                mainActivity.showSnackbar("게시물이 삭제되었습니다.")
            }
        }

        // 댓글 등록
        initCommentSaveResult()
        commentSaveResult.observe(viewLifecycleOwner) {
            if (it.commentId == 0L) {
                // 댓글 등록 실패
                mainActivity.showSnackbar("댓글 등록에 실패하였습니다.")
            } else {
                commentDialog.addComment(it)
            }
            commentDialog.clearEditText()
        }

        // 댓글 삭제
        initIsCommentDeleted()
        isCommentDeleted.observe(viewLifecycleOwner) {
            if (!it) {
                // 댓글 삭제 실패
                mainActivity.showSnackbar("댓글 삭제에 실패하였습니다.")
            } else {
                // 댓글 삭제 성공
            }
            optionDialog.dismiss()
        }

        // 해시태그 검색
        searchedBoards.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                mainActivity.showSnackbar("해당하는 해시태그의 게시물이 없습니다.")
            }
            searchBoardAdapter.setBoards(it)
        }
    }
}
