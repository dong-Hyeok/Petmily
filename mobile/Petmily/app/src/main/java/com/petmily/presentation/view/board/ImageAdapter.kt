package com.petmily.presentation.view.board

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemBoardSelectImageBinding
import com.petmily.repository.dto.Photo

class ImageAdapter :
    RecyclerView.Adapter<ImageAdapter.CustomViewHolder>() {

    private var imgs = mutableListOf<Photo>()

    inner class CustomViewHolder(private val binding: ItemBoardSelectImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(img: Photo) = with(binding) {
            Glide.with(itemView)
                .load(img.imgUrl)
                .into(ivSelectedImg)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ImageAdapter.CustomViewHolder {
        return CustomViewHolder(ItemBoardSelectImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageAdapter.CustomViewHolder, position: Int) {
        holder.bindInfo(imgs[position])
    }

    override fun getItemCount(): Int {
        return imgs.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setImgs(imgs: MutableList<Photo>) {
        this.imgs = imgs
        notifyDataSetChanged()
    }
}
