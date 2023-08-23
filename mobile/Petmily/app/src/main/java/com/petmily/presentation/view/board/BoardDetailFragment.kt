package com.petmily.presentation.view.board

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentBoardDetailBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.dialog.CommentDialog
import com.petmily.presentation.view.dialog.OptionDialog
import com.petmily.presentation.view.home.BoardImgAdapter
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.util.StringFormatUtil

class BoardDetailFragment :
    BaseFragment<FragmentBoardDetailBinding>(FragmentBoardDetailBinding::bind, R.layout.fragment_board_detail) {

    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var boardImgAdapter: BoardImgAdapter

    // 댓글 BottomSheetDialog
    private val commentDialog by lazy { CommentDialog(mainActivity, mainViewModel, boardViewModel) }

    // 3점(옵션) BottomSheetDialog
    private val optionDialog by lazy { OptionDialog(mainActivity, mainViewModel, boardViewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initObserver()
    }

    private fun initBtn(board: Board) = with(binding) {
        // 뒤로가기 버튼 클릭
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 좋아요
        val likeAnimation by lazy {
            ScaleAnimation(
                0.7f,
                1.0f,
                0.7f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.7f,
                Animation.RELATIVE_TO_SELF,
                0.7f,
            ).apply {
                duration = 500
                interpolator = BounceInterpolator()
            }
        }
        btnLike.setOnCheckedChangeListener { compoundButton, isClicked ->
            compoundButton.startAnimation(likeAnimation)
            if (isClicked) {
                // 좋아요 등록
                boardViewModel.registerHeart(
                    board.boardId,
                    ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                )
            } else {
                // 좋아요 취소
                boardViewModel.deleteHeart(
                    board.boardId,
                    ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                )
            }
        }

        // 댓글
        ivComment.setOnClickListener {
            commentDialog.showCommentDialog(board)
        }

        // 프로필 클릭
        ivProfile.setOnClickListener {
            userViewModel.selectedUserLoginInfoDto = UserLoginInfoDto(userEmail = board.userEmail)
            mainActivity.changeFragment("my page")
        }

        // 옵션(3점) 클릭
        ivOption.setOnClickListener {
            boardViewModel.selectedBoard = board
            optionDialog.showBoardOptionDialog()
        }
    }

    private fun initView(board: Board) = with(binding) {
        tvName.text = board.userNickname
        tvCommentContent.text = board.boardContent
        tvUploadDate.text = StringFormatUtil.uploadDateFormat(board.boardUploadTime)
        btnLike.isChecked = board.likedByCurrentUser
        tvLikeCnt.text = StringFormatUtil.likeCntFormat(board.heartCount)

        // 색상 설정
        try {
            if (board.userRing.isNullOrBlank()) board.userRing = "ffffff"
            clMypageUserImage.setBackgroundColor(Color.parseColor("#${board.userRing}"))
        } catch (e: Exception) {
            clMypageUserImage.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        // 프로필 이미지
        Glide.with(mainActivity)
            .load(board.userProfileImageUrl)
            .into(ivProfile)

        // 사진이 없을 경우 공간 제거
        if (board.photoUrls.isEmpty()) {
            vpBoardImg.visibility = View.GONE
        } else {
            vpBoardImg.visibility = View.VISIBLE
        }

        // 내 피드일 경우 3점(옵션)버튼 보이게
        if (board.userEmail == ApplicationClass.sharedPreferences.getString("userEmail")) {
            ivOption.visibility = View.VISIBLE
        } else {
            ivOption.visibility = View.GONE
        }
    }

    private fun initData() {
        boardViewModel.selectOneBoard(
            boardViewModel.selectedBoard.boardId,
            ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
        )
    }

    private fun initObserver() = with(boardViewModel) {
        // 단일 게시물 조회
        boardViewModel.selectOneBoard.observe(viewLifecycleOwner) {
            if (it.boardId == 0L) {
                mainActivity.showSnackbar("해당 게시물이 삭제되었습니다.")
                parentFragmentManager.popBackStack()
            } else {
                initBtn(it)
                initImgViewPager(it)
                initView(it)
            }
        }

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
    }

    private fun initImgViewPager(board: Board) = with(binding) {
        boardImgAdapter = BoardImgAdapter(board.photoUrls)
        vpBoardImg.adapter = boardImgAdapter

        // 이미지 순서에 따른 하단 점 설정, img 데이터 설정 이후에 설정해야 오류 없음
        ciBoardImg.setViewPager(vpBoardImg)
    }
}
