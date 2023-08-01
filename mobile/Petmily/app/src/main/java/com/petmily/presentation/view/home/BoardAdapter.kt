package com.petmily.presentation.view.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.petmily.R
import com.petmily.databinding.ItemBoardBinding
import com.petmily.repository.dto.Board

class BoardAdapter(
    private var boards: List<Board> = listOf(),
) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
    
    // 이전 게시글 인덱스, 스크롤 시 감지하여 애니메이션 처리
    private var prevPos = 0
    
    private lateinit var boardImgAdapter: BoardImgAdapter
    
    // 이미지 데이터 TODO: api 통신 후 적용되는 실제 데이터로 변경
    private val imgs = listOf(
        "",
        "",
        "",
        "",
        "",
    )
    
    inner class BoardViewHolder(val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(board: Board) = with(binding) {
            // TODO: data binding
            initAdapter(binding)
    
            // 좋아요, 북마크 아이콘 클릭 애니메이션
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
            val bookmarkAnimation by lazy {
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
            
            btnLike.setOnCheckedChangeListener { compoundButton, b -> // 좋아요 버튼 (토글 버튼)
                compoundButton.startAnimation(likeAnimation)
                boardClickListener.likeClick(compoundButton, binding, board, layoutPosition)
            }
            ivComment.setOnClickListener { // 댓글 버튼
                boardClickListener.commentClick(binding, board, layoutPosition)
            }
            btnBookmark.setOnCheckedChangeListener { compoundButton, b -> // 북마크 버튼 (토글 버튼)
                compoundButton.startAnimation(bookmarkAnimation)
                boardClickListener.bookmarkClick(compoundButton, binding, board, layoutPosition)
            }
            ivProfile.setOnClickListener {
                boardClickListener.profileClick(binding, board, layoutPosition)
            }
            tvName.setOnClickListener {
                boardClickListener.profileClick(binding, board, layoutPosition)
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
    
    private fun initAdapter(binding: ItemBoardBinding) = with(binding) {
        boardImgAdapter = BoardImgAdapter().apply {
            setImgs(imgs)
        }
        vpBoardImg.adapter = boardImgAdapter
    }
    
    @SuppressLint("NotifyDataSetChanged")
    fun setBoards(boards: List<Board>) {
        this.boards = boards
        notifyDataSetChanged()
    }
    
    // 이벤트 처리 listener
    interface BoardClickListener {
        fun likeClick(compoundButton: CompoundButton, binding: ItemBoardBinding, board: Board, position: Int)
        fun commentClick(binding: ItemBoardBinding, board: Board, position: Int)
        fun bookmarkClick(compoundButton: CompoundButton, binding: ItemBoardBinding, board: Board, position: Int)
        fun profileClick(binding: ItemBoardBinding, board: Board, position: Int)
    }
    private lateinit var boardClickListener: BoardClickListener
    fun setBoardClickListener(boardClickListener: BoardClickListener) {
        this.boardClickListener = boardClickListener
    }
}
