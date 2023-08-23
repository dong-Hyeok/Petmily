package com.petmily.presentation.view.walk

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemWalkListBinding
import com.petmily.repository.dto.WalkInfo
import com.petmily.util.StringFormatUtil

class WalkListAdapter : RecyclerView.Adapter<WalkListAdapter.WalkListViewHolder>() {

    private var walkInfoList = listOf<WalkInfo>()

    inner class WalkListViewHolder(private val binding: ItemWalkListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(walkInfo: WalkInfo) = with(binding) {
            Glide.with(itemView)
                .load(walkInfo.pet.petImg)
                .into(ivMypageUserImage)
            tvWalkPetName.text = walkInfo.pet.petName
            tvWalkTime.text = StringFormatUtil.distanceIntToString(walkInfo.walkDistance)
            tvWalkDist.text = StringFormatUtil.timeIntToString(walkInfo.walkSpend)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): WalkListViewHolder {
        return WalkListViewHolder(
            ItemWalkListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(holder: WalkListViewHolder, position: Int) {
        holder.bindInfo(walkInfoList[position])
    }

    override fun getItemCount(): Int {
        return walkInfoList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setWalkInfoList(walkInfoList: List<WalkInfo>) {
        this.walkInfoList = walkInfoList
        notifyDataSetChanged()
    }
}
