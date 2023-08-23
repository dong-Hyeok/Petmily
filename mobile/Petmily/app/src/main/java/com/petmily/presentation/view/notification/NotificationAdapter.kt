package com.petmily.presentation.view.notification

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.petmily.R
import com.petmily.databinding.ItemNotificationBinding
import com.petmily.repository.dto.ResponseNotification
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationAdapter(
    private var notis: MutableList<ResponseNotification> = mutableListOf(),
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(noti: ResponseNotification) = with(binding) {
            when (noti.notiType) {
                "FOLLOW" -> {
                    tvNotiMsg.text = "${noti.fromUserNickname}님이 회원님을 팔로우 했습니다."
                    ivNotiIcon.setBackgroundResource(R.drawable.ic_cat)
                }
                "COMMENT" -> {
                    tvNotiMsg.text = "${noti.fromUserNickname}님이 회원님의 게시글에 댓글을 남겼습니다."
                    ivNotiIcon.setBackgroundResource(R.drawable.ic_comment)
                }
                "LIKE" -> {
                    tvNotiMsg.text = "${noti.fromUserNickname}님이 회원님의 게시글에 좋아요를 눌렀습니다."
                    ivNotiIcon.setBackgroundResource(R.drawable.ic_feed_favorite)
                }
            }
            val time = timeDifference(noti.createDate)
            tvNotiTime.text = time

            root.setOnClickListener {
                notificationClickListener.notificationClick(binding, noti, layoutPosition)
            }
        }
    }

    /**
     * 현재시간 - 마지막 채팅 시간
     */
    fun timeDifference(time: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        val inputDateTime = dateFormat.parse(time)

        val timeDifferenceInMillis = Math.abs(currentTime.time - inputDateTime.time)
        val days = timeDifferenceInMillis / (24 * 60 * 60 * 1000)
        val hours = (timeDifferenceInMillis / (60 * 60 * 1000)) % 24
        val minutes = (timeDifferenceInMillis / (60 * 1000)) % 60

        val timeDiff = when {
            days > 0 -> "$days 일 전"
            hours > 0 -> "$hours 시간 전"
            minutes > 0 -> "$minutes 분 전"
            else -> "방금 전"
        }

        return timeDiff
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bindInfo(notis[position])
    }

    override fun getItemCount(): Int {
        return notis.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNotis(notis: MutableList<ResponseNotification>) {
        this.notis = notis
        notifyDataSetChanged()
    }

    // 이벤트 처리 listener
    interface NotificationClickListener {
        fun notificationClick(binding: ItemNotificationBinding, noti: ResponseNotification, position: Int)
    }
    private lateinit var notificationClickListener: NotificationClickListener
    fun setNotificationListClickListener(notificationClickListener: NotificationClickListener) {
        this.notificationClickListener = notificationClickListener
    }
}
