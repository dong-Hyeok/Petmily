package com.petmily.presentation.view.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.config.ApplicationClass
import com.petmily.databinding.ItemCommentBinding
import com.petmily.repository.dto.Comment
import com.petmily.util.StringFormatUtil

class ReplyAdapter(
    private var replys: List<Comment> = listOf(),
) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    inner class ReplyViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(reply: Comment) {
            initView(binding, reply, layoutPosition, itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        return ReplyViewHolder(
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
        return replys.size
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        holder.bindInfo(replys[position])
    }

    private fun initView(binding: ItemCommentBinding, reply: Comment, layoutPosition: Int, itemView: View) = with(binding) {
        Glide.with(itemView)
            .load(reply.userProfileImg)
            .into(ivProfile)
        tvCommentContent.text = reply.commentContent
        tvName.text = reply.userNickname
        tvOpenReply.visibility = View.GONE
        tvUploadDate.text = StringFormatUtil.uploadDateFormat(reply.commentTime)

        // 3점(옵션), 내 글에만 보이게
        ivOption.visibility = if (reply.userEmail == ApplicationClass.sharedPreferences.getString("userEmail")) {
            View.VISIBLE
        } else {
            View.GONE
        }
        ivOption.setOnClickListener {
            replyClickListener.optionClick(binding, reply, layoutPosition)
        }
    }

    // 이벤트 처리 listener
    interface ReplyClickListener {
        fun optionClick(binding: ItemCommentBinding, reply: Comment, position: Int)
    }
    private lateinit var replyClickListener: ReplyClickListener
    fun setReplyClickListener(replyClickListener: ReplyClickListener) {
        this.replyClickListener = replyClickListener
    }
}
