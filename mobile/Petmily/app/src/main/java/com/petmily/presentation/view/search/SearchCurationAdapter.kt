package com.petmily.presentation.view.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemSearchCurationBinding
import com.petmily.repository.dto.Curation

class SearchCurationAdapter(
    private var curations: List<Curation> = listOf(),
) : RecyclerView.Adapter<SearchCurationAdapter.SearchCurationViewHolder>() {

    // 내 마이페이지인지 여부
    private var isMyPage = false

    inner class SearchCurationViewHolder(val binding: ItemSearchCurationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(curation: Curation) = with(binding) {
            Glide.with(itemView)
                .load(curation.cimage)
                .into(ivCurationItemImage)
            tvCurationItemTitle.text = curation.ctitle
            root.setOnClickListener {
                curationClickListener.curationClick(binding, curation, layoutPosition)
            }

            // 북마크 버튼
            if (isMyPage) {
                btnBookmark.visibility = View.VISIBLE
                btnBookmark.isChecked = true
                btnBookmark.setOnClickListener {
                    curationClickListener.bookmarkClick(binding, curation, layoutPosition)
                }
            } else {
                btnBookmark.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCurationViewHolder {
        return SearchCurationViewHolder(
            ItemSearchCurationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun getItemCount(): Int = curations.size

    override fun onBindViewHolder(holder: SearchCurationViewHolder, position: Int) {
        holder.bindInfo(curations[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCurations(curations: List<Curation>, isMyPage: Boolean) {
        this.curations = curations
        this.isMyPage = isMyPage
        notifyDataSetChanged()
    }

    // 이벤트 처리 listener
    interface CurationClickListener {
        fun curationClick(binding: ItemSearchCurationBinding, curation: Curation, position: Int)
        fun bookmarkClick(binding: ItemSearchCurationBinding, curation: Curation, position: Int)
    }
    private lateinit var curationClickListener: CurationClickListener
    fun setCurationClickListener(curationClickListener: CurationClickListener) {
        this.curationClickListener = curationClickListener
    }
}
