package com.petmily.presentation.view.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.databinding.ItemCommentBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.repository.dto.Comment
import com.petmily.util.StringFormatUtil

class CommentAdapter(
    private val mainActivity: MainActivity,
    private var comments: List<Comment> = listOf(),
    private var allComments: List<Comment> = listOf(),
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(comment: Comment) = with(binding) {
            tvOpenReply.setOnClickListener {
                if (rcvReply.visibility == View.VISIBLE) {
                    rcvReply.visibility = View.GONE
                    tvOpenReply.text = mainActivity.getString(R.string.comment_tv_open_reply)
                } else {
                    rcvReply.visibility = View.VISIBLE
                    tvOpenReply.text = mainActivity.getString(R.string.comment_tv_close_reply)
                }
            }
            initView(binding, comment, itemView, layoutPosition)
            initAdapter(binding, comment, layoutPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(
                    parent.context,
                ),
                parent,
                false,
            ),
        )
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bindInfo(comments[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setComments(comments: List<Comment>) {
        // 답글이 아닌 일반 댓글만 출력
        this.allComments = comments
        this.comments = comments.filter { it.parentId == null }
        notifyDataSetChanged()
    }

    /**
     * 댓글을 입력할 경우 리스트에 추가
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addComment(comment: Comment) {
        this.allComments = this.allComments + comment
        this.comments = allComments.filter { it.parentId == null }
        notifyDataSetChanged()
    }

    /**
     * 댓글을 삭제할 경우 리스트에서 삭제
     */
    @SuppressLint("NotifyDataSetChanged")
    fun removeComment(commentId: Long) {
        this.allComments = this.allComments.filterNot { it.commentId == commentId }
        this.comments = allComments.filter { it.parentId == null }
        notifyDataSetChanged()
    }

    private fun initView(binding: ItemCommentBinding, comment: Comment, itemView: View, layoutPosition: Int) = with(binding) {
        Glide.with(itemView)
            .load(comment.userProfileImg)
            .into(ivProfile)
        tvName.text = comment.userNickname
        tvUploadDate.text = StringFormatUtil.uploadDateFormat(comment.commentTime)
        tvCommentContent.text = comment.commentContent

        // 3점(옵션), 내 글에만 보이게
        ivOption.visibility = if (comment.userEmail == ApplicationClass.sharedPreferences.getString("userEmail")) {
            View.VISIBLE
        } else {
            View.GONE
        }
        ivOption.setOnClickListener {
            commentClickListener.optionClick(binding, comment, layoutPosition)
        }

        // 댓글 클릭 시 답글 달기
        root.setOnClickListener {
            commentClickListener.commentClick(binding, comment, layoutPosition)
        }
    }

    private fun initAdapter(binding: ItemCommentBinding, comment: Comment, layoutPosition: Int) = with(binding) {
        val replyAdapter = ReplyAdapter(allComments.filter { it.parentId == comment.commentId }).apply {
            setReplyClickListener(object : ReplyAdapter.ReplyClickListener {
                override fun optionClick(
                    binding: ItemCommentBinding,
                    reply: Comment,
                    position: Int,
                ) {
                    commentClickListener.optionClick(binding, reply, layoutPosition)
                }
            })
        }
        rcvReply.apply {
            adapter = replyAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    // 이벤트 처리 listener
    interface CommentClickListener {
        fun commentClick(binding: ItemCommentBinding, comment: Comment, position: Int)
        fun optionClick(binding: ItemCommentBinding, comment: Comment, position: Int)
    }
    private lateinit var commentClickListener: CommentClickListener
    fun setCommentClickListener(commentClickListener: CommentClickListener) {
        this.commentClickListener = commentClickListener
    }
}
