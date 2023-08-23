package com.petmily.presentation.view.curation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemCurationBinding
import com.petmily.repository.dto.Curation

class CurationAdapter(
    private val curationList: MutableList<Curation>?,
    private val userBookmarkList: HashSet<Long>,
) :
    RecyclerView.Adapter<CurationAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(binding: ItemCurationBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvCurationItemTitle
        private val image = binding.ivCurationItemImage
        private val cdCuration = binding.cdCuration
        private val btnBookmark = binding.btnBookmark

        fun bindInfo(curation: Curation) {
            // 좋아요 아이콘 클릭 애니메이션
            val likeAnimation by lazy {
                ScaleAnimation(
                    0.7f,
                    1.0f,
                    0.7f,
                    1.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.7f,
                    Animation.RELATIVE_TO_SELF,
                    0.7f,
                ).apply {
                    duration = 500
                    interpolator = BounceInterpolator()
                }
            }

            title.text = curation.ctitle

            Glide.with(itemView.context)
                .load(curation.cimage)
                .into(image)

            // 북마크 체크 상태
            if (userBookmarkList.contains(curation.cid)) {
                btnBookmark.isChecked = true
            }

            // webView이동 클릭 이벤트
            cdCuration.setOnClickListener {
                itemClickListener.itemClick(it, curation, layoutPosition)
            }

            // bookmart 클릭 이벤트
            btnBookmark.setOnCheckedChangeListener { compoundButton, isChecked -> // 좋아요 버튼 (토글 버튼)
                compoundButton.startAnimation(likeAnimation)
                itemClickListener.bookmarkClick(compoundButton, isChecked, curation, layoutPosition)
            }
        }
    }

    // click Event
    interface ItemClickListener {
        fun itemClick(view: View, curation: Curation, position: Int)

        fun bookmarkClick(view: View, isChecked: Boolean, curation: Curation, position: Int)
    }
    lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CustomViewHolder {
        return CustomViewHolder(ItemCurationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CurationAdapter.CustomViewHolder, position: Int) {
        holder.bindInfo(curationList?.get(position) ?: Curation())
    }

    override fun getItemCount(): Int {
        return curationList?.size ?: 0
    }
}
