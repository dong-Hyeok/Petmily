package com.petmily.presentation.view.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.databinding.ItemBoardBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.repository.dto.Board
import com.petmily.util.StringFormatUtil

private const val TAG = "Fetmily_BoardAdapter"
class BoardAdapter(
    private val mainActivity: MainActivity,
    private var boards: List<Board> = listOf(),
) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    // 이전 게시글 인덱스, 스크롤 시 감지하여 애니메이션 처리
    private var prevPos = 0

    private lateinit var boardImgAdapter: BoardImgAdapter

    inner class BoardViewHolder(val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(board: Board) = with(binding) {
            initView(binding, board, itemView)
            initAdapter(binding, board.photoUrls)

            // 좋아요 아이콘 클릭 애니메이션
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

            ciBoardImg.setViewPager(vpBoardImg)

            btnLike.setOnCheckedChangeListener { compoundButton, isClicked -> // 좋아요 버튼 (토글 버튼)
                compoundButton.startAnimation(likeAnimation)
                boardClickListener.heartClick(isClicked, binding, board, layoutPosition)
            }
            ivComment.setOnClickListener { // 댓글 버튼
                boardClickListener.commentClick(binding, board, layoutPosition)
            }
            ivProfile.setOnClickListener {
                boardClickListener.profileClick(binding, board, layoutPosition)
            }
            tvName.setOnClickListener {
                boardClickListener.profileClick(binding, board, layoutPosition)
            }
            ivOption.setOnClickListener {
                boardClickListener.optionClick(binding, board, layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        return BoardViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return boards.size
    }

    override fun onBindViewHolder(holder: BoardViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bindInfo(boards[position])

        if (position >= prevPos) {
            holder.binding.clBoard.animation = AnimationUtils.loadAnimation(holder.binding.clBoard.context, R.anim.list_item_anim_from_right)
        } else {
            holder.binding.clBoard.animation = AnimationUtils.loadAnimation(holder.binding.clBoard.context, R.anim.list_item_anim_from_left)
        }
        prevPos = position
    }

    private fun initAdapter(binding: ItemBoardBinding, imgs: List<String>) = with(binding) {
        boardImgAdapter = BoardImgAdapter(mainActivity, imgs)
        vpBoardImg.adapter = boardImgAdapter
    }

    private fun initView(binding: ItemBoardBinding, board: Board, itemView: View) = with(binding) {
        tvName.text = board.userNickname
        tvCommentContent.text = board.boardContent
        tvUploadDate.text = StringFormatUtil.uploadDateFormat(board.boardUploadTime)
        btnLike.isChecked = board.likedByCurrentUser
        tvLikeCnt.text = StringFormatUtil.likeCntFormat(board.heartCount)

        /**
         * todo 프로필 링 Color (constraintLayout 색 변경해야함)
         */
//        clMypageUserImage.setBackgroundColor(mainActivity.resources.getColor(R.color.favorate_red))

        // 프로필 이미지
        Glide.with(itemView)
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

    @SuppressLint("NotifyDataSetChanged")
    fun setBoards(boards: List<Board>) {
        this.boards = boards
        notifyDataSetChanged()
    }

    // 이벤트 처리 listener
    interface BoardClickListener {
        fun heartClick(isClicked: Boolean, binding: ItemBoardBinding, board: Board, position: Int)
        fun commentClick(binding: ItemBoardBinding, board: Board, position: Int)
        fun profileClick(binding: ItemBoardBinding, board: Board, position: Int)
        fun optionClick(binding: ItemBoardBinding, board: Board, position: Int)
    }
    private lateinit var boardClickListener: BoardClickListener
    fun setBoardClickListener(boardClickListener: BoardClickListener) {
        this.boardClickListener = boardClickListener
    }
}
