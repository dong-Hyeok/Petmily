package com.petmily.presentation.view.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemChatOtherBinding
import com.petmily.databinding.ItemChatSelfBinding
import com.petmily.repository.dto.Chat
import com.petmily.repository.dto.MypageInfo

class ChatDetailAdapter(val other: MypageInfo, val selfEmail: String ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SELF = 1
    private val ITEM_OTHER = 2

    private val diffcalback = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffcalback)

    // chat data를 갱신
    fun submitChat(chats: List<Chat>) {
        differ.submitList(chats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SELF) {
            val binding = ItemChatSelfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SelfChatItemViewHolder(binding)
        } else {
            val binding = ItemChatOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            OtherChatItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = differ.currentList[position]

        if (chat.writer == selfEmail) {
            (holder as SelfChatItemViewHolder).bind(chat)
        } else {
            (holder as OtherChatItemViewHolder).bind(chat)
        }
    }

    inner class OtherChatItemViewHolder(val binding: ItemChatOtherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                Glide.with(itemView)
                    .load(other.userProfileImg)
                    .circleCrop()
                    .into(ivProfile)

                tvTime.text = chat.createdAt
                tvUserNickname.text = other.userNickname
                tvMessage.text = chat.message
            }
        }
    }

    inner class SelfChatItemViewHolder(val binding: ItemChatSelfBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                tvTime.text = chat.createdAt
                tvMessage.text = chat.message
            }
        }
    }

    // 각 채팅에 대해 어떤 뷰 홀더를 사용할지? (여기서는 other / self)
    override fun getItemViewType(position: Int): Int {
        val chat = differ.currentList[position]
        return if (chat.writer == selfEmail) ITEM_SELF else ITEM_OTHER
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
