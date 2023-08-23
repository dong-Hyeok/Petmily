package com.petmily.presentation.view.search

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.petmily.R
import com.petmily.databinding.ItemSearchBoardBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.home.BoardImgAdapter
import com.petmily.repository.dto.Board
import com.petmily.util.StringFormatUtil

class SearchBoardAdapter(
    private val mainActivity: MainActivity,
    private var boards: List<Board> = listOf(),
) : RecyclerView.Adapter<SearchBoardAdapter.SearchBoardViewHolder>() {

    // 이전 게시글 인덱스, 스크롤 시 감지하여 애니메이션 처리
    private var prevPos = 0

    private lateinit var boardImgAdapter: BoardImgAdapter

    inner class SearchBoardViewHolder(val binding: ItemSearchBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(board: Board) = with(binding) {
            initView(binding, board, itemView)
            initAdapter(binding, board.photoUrls)

            ciBoardImg.setViewPager(vpBoardImg)

            ivProfile.setOnClickListener {
                boardClickListener.profileClick(binding, board, layoutPosition)
            }
            tvName.setOnClickListener {
                boardClickListener.profileClick(binding, board, layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchBoardViewHolder {
        return SearchBoardViewHolder(
            ItemSearchBoardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun getItemCount(): Int {
        return boards.size
    }

    override fun onBindViewHolder(holder: SearchBoardViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bindInfo(boards[position])

        if (position >= prevPos) {
            holder.binding.clBoard.animation = AnimationUtils.loadAnimation(holder.binding.clBoard.context, R.anim.list_item_anim_from_right)
        } else {
            holder.binding.clBoard.animation = AnimationUtils.loadAnimation(holder.binding.clBoard.context, R.anim.list_item_anim_from_left)
        }
        prevPos = position
    }

    private fun initAdapter(binding: ItemSearchBoardBinding, imgs: List<String>) = with(binding) {
        boardImgAdapter = BoardImgAdapter(imgs)
        vpBoardImg.adapter = boardImgAdapter
    }

    private fun initView(binding: ItemSearchBoardBinding, board: Board, itemView: View) = with(binding) {
        tvName.text = board.userNickname
        tvCommentContent.text = board.boardContent
        tvUploadDate.text = StringFormatUtil.uploadDateFormat(board.boardUploadTime)

        // 색상 설정
        try {
            if (board.userRing.isNullOrBlank()) board.userRing = "ffffff"
            clMypageUserImage.setBackgroundColor(Color.parseColor("#${board.userRing}"))
        } catch (e: Exception) {
            clMypageUserImage.setBackgroundColor(Color.parseColor("#ffffff"))
        }

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

        // 해시태그
        chipGroup.removeAllViews()
        board.hashTags.forEach {
            val chip = createChip(it)
            chipGroup.addView(chip)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createChip(tag: String): Chip {
        val chip = Chip(mainActivity, null, R.style.CustomChip)
        chip.apply {
            text = "# $tag"
            setChipBackgroundColorResource(R.color.main_color)
            setTextColor(ContextCompat.getColor(mainActivity, R.color.white))
        }

        return chip
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBoards(boards: List<Board>) {
        this.boards = boards
        notifyDataSetChanged()
    }

    // 이벤트 처리 listener
    interface BoardClickListener {
        fun profileClick(binding: ItemSearchBoardBinding, board: Board, position: Int)
    }
    private lateinit var boardClickListener: BoardClickListener
    fun setBoardClickListener(boardClickListener: BoardClickListener) {
        this.boardClickListener = boardClickListener
    }
}
