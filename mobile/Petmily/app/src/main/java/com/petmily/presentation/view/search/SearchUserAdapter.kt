package com.petmily.presentation.view.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemSearchUserBinding
import com.petmily.repository.dto.UserLoginInfoDto

class SearchUserAdapter(
    private var userLoginInfoDtos: List<UserLoginInfoDto> = listOf(),
) : RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>() {

    inner class SearchUserViewHolder(val binding: ItemSearchUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(userLoginInfoDto: UserLoginInfoDto) = with(binding) {
            Glide.with(itemView)
                .load(userLoginInfoDto.userProfileImg)
                .into(ivProfileImg)
            tvName.text = userLoginInfoDto.userNickname

            // 프로필 클릭 시 해당 상세 프로필로 이동
            root.setOnClickListener {
                userClickListener.userClick(binding, userLoginInfoDto, layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        return SearchUserViewHolder(
            ItemSearchUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun getItemCount(): Int {
        return userLoginInfoDtos.size
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        holder.bindInfo(userLoginInfoDtos[position])
    }

    // 이벤트 처리 listener
    interface UserClickListener {
        fun userClick(binding: ItemSearchUserBinding, userLoginInfoDto: UserLoginInfoDto, position: Int)
    }
    private lateinit var userClickListener: UserClickListener
    fun setUserClickListener(userClickListener: UserClickListener) {
        this.userClickListener = userClickListener
    }
}
