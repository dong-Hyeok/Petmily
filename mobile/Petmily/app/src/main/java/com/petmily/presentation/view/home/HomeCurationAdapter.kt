package com.petmily.presentation.view.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemHomeCurationBinding
import com.petmily.presentation.viewmodel.CurationViewModel
import com.petmily.repository.dto.Curation

class HomeCurationAdapter(
    val curationViewModel: CurationViewModel,
) : RecyclerView.Adapter<HomeCurationAdapter.HomeCurationViewHolder>() {

    var curationList = curationViewModel.randomCurationList.value

    inner class HomeCurationViewHolder(val binding: ItemHomeCurationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(curation: Curation) = with(binding) {
            tvCurationItemTitle.text = curation.ctitle

            Glide.with(itemView.context)
                .load(curation.cimage)
                .into(ivCurationItemImage)

            root.setOnClickListener {
                curationClickListener.curationClick(binding, curation, layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCurationViewHolder {
        return HomeCurationViewHolder(ItemHomeCurationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return curationList?.size ?: 0
    }

    override fun onBindViewHolder(holder: HomeCurationViewHolder, position: Int) {
        holder.bindInfo(curationList?.get(position) ?: Curation())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurations(curationList: MutableList<Curation>?) {
        this.curationList = (listOf(curationList!!.last()) + curationList + listOf(curationList.first())).toMutableList()
        notifyDataSetChanged()
    }

    // 이벤트 처리 listener
    interface CurationClickListener {
        fun curationClick(binding: ItemHomeCurationBinding, curation: Curation, position: Int)
    }
    private lateinit var curationClickListener: CurationClickListener
    fun setCurationClickListener(curationClickListener: CurationClickListener) {
        this.curationClickListener = curationClickListener
    }
}
