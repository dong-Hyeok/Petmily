package com.petmily.presentation.view.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.databinding.DialogCommentBinding
import com.petmily.databinding.ItemCommentBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.home.CommentAdapter
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.Comment
import com.petmily.util.StringFormatUtil

class CommentDialog(
    private val mainActivity: MainActivity,
    private val mainViewModel: MainViewModel,
    private val boardViewModel: BoardViewModel,
) : BottomSheetDialog(mainActivity) {

    private val binding: DialogCommentBinding by lazy {
        DialogCommentBinding.inflate(layoutInflater)
    }

    private val optionDialog by lazy { OptionDialog(mainActivity, mainViewModel, boardViewModel) }

    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Dialog 자체 window가 wrap_content이므로 match_parent로 변경
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun initCommentAdapter() = with(binding) {
        commentAdapter = CommentAdapter(mainActivity).apply {
            setCommentClickListener(object : CommentAdapter.CommentClickListener {
                override fun commentClick(
                    binding: ItemCommentBinding,
                    comment: Comment,
                    position: Int,
                ) {
                    initCommentDialogForReply(comment)
                }

                override fun optionClick(
                    binding: ItemCommentBinding,
                    comment: Comment,
                    position: Int,
                ) {
                    boardViewModel.selectedComment = comment
                    optionDialog.showCommentOptionDialog()
                }
            })
        }
        rcvComment.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun initListener() = with(binding) {
        // 댓글 작성 중 게시 버튼 활성화
        etComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (etComment.length() > 0 && tvCommentUpload.visibility == View.GONE) {
                    tvCommentUpload.visibility = View.VISIBLE
                } else if (etComment.length() == 0) {
                    tvCommentUpload.visibility = View.GONE
                }
            }
        })

        // 댓글 작성 버튼
        tvCommentUpload.setOnClickListener {
            val comment = Comment(
                userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                boardId = boardViewModel.selectedBoard.boardId,
                commentContent = etComment.text.toString(),
            ).apply {
                // 답글이면 parentId 값 지정
                if (boardViewModel.selectedComment.parentId != null) {
                    // 답글에서 태그를 누르면 답글의 부모 댓글 기준으로 태그
                    this.parentId = boardViewModel.selectedComment.parentId
                } else if (boardViewModel.selectedComment.commentId != 0L) {
                    // 답글이 아닌 일반 댓글에서 태그
                    this.parentId = boardViewModel.selectedComment.commentId
                } else {
                    this.parentId = null
                }
            }
            boardViewModel.saveComment(comment, mainViewModel)
        }

        // 답글 작성 중 제거
        tvReplyWritingClose.setOnClickListener {
            clReplyWriting.visibility = View.GONE
            boardViewModel.selectedComment = Comment()
        }
    }

    fun showCommentDialog(board: Board) = with(binding) {
        initCommentAdapter()
        initListener()

        // 댓글 Dialog 내 게시글 정보 binding
        Glide.with(root)
            .load(board.userProfileImageUrl)
            .into(ivProfile)
        tvName.text = board.userNickname
        tvCommentContent.text = board.boardContent
        tvLikeCnt.text = StringFormatUtil.likeCntFormat(board.heartCount)

        btnLike.isChecked = board.likedByCurrentUser
        btnLike.setOnCheckedChangeListener { _, isClicked ->
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

        // 댓글 리스트
        commentAdapter.setComments(board.comments)

        // 선택한 게시글 설정
        boardViewModel.selectedBoard = board

        etComment.text!!.clear()
        clReplyWriting.visibility = View.GONE

        // 선택한 댓글 초기화
        boardViewModel.selectedComment = Comment()

        // 옵션 dialog에서 댓글 태그 버튼 클릭
        optionDialog.setOptionDialogClickListener(object : OptionDialog.OptionDialogClickListener {
            override fun commentTagClick() {
                initCommentDialogForReply(boardViewModel.selectedComment)
            }
            override fun commentRemoveClick() {
                commentAdapter.removeComment(boardViewModel.selectedComment.commentId)
            }
        })

        show()
    }

    private fun initCommentDialogForReply(comment: Comment) = with(binding) {
        clReplyWriting.visibility = View.VISIBLE
        tvReplyWriting.text = String.format("${comment.userNickname}%s", mainActivity.getString(R.string.comment_tv_reply_writing))

        boardViewModel.selectedComment = comment
    }

    fun clearEditText() = with(binding) {
        etComment.text!!.clear()
    }

    fun addComment(comment: Comment) {
        commentAdapter.addComment(comment)
    }
}
