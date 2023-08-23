package com.petmily.presentation.view.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemBoardImgBinding
import com.petmily.presentation.view.MainActivity

class BoardImgAdapter(
    private var imgs: List<String> = listOf(),
) : RecyclerView.Adapter<BoardImgAdapter.BoardImgViewHolder>() {
    inner class BoardImgViewHolder(val binding: ItemBoardImgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(imgUrl: String) {
            initView(binding, itemView, imgUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardImgViewHolder {
        return BoardImgViewHolder(ItemBoardImgBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imgs.size
    }

    override fun onBindViewHolder(holder: BoardImgViewHolder, position: Int) {
        holder.bindInfo(imgs[position])
    }

    private fun initView(binding: ItemBoardImgBinding, itemView: View, imgUrl: String) = with(binding) {
        Glide.with(itemView.context)
            .load(imgUrl)
            .into(ivBoardImg)
    }
}
