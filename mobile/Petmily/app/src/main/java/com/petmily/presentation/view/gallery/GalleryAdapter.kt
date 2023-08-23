package com.petmily.presentation.view.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.petmily.databinding.ItemGalleryPhotoBinding
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.repository.dto.Photo

class GalleryAdapter(
    private var lifecycleOwner: LifecycleOwner,
    private var mainViewModel: MainViewModel,
) :
    RecyclerView.Adapter<GalleryAdapter.GalleryHolder>() {

    private val fromFragment = mainViewModel.getFromGalleryFragment()
    private lateinit var selectedPhoto: Photo
    private val limitSelectCnt = 1
    private var currentSelectCnt = 0

    inner class GalleryHolder(binding: ItemGalleryPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemImage = binding.ivItemImage
        val checkBox = binding.cbBox

        fun bindInfo(product: Photo) {
            product.isSelected.observe(lifecycleOwner) { isSelected ->
                // isSelected 값이 변경되었을 때 실행되는 코드
                checkBox.isChecked = isSelected
                if (checkBox.isChecked) {
                    itemImage.alpha = 0.5f
                } else {
                    itemImage.alpha = 1.0f
                }
            }

            Glide.with(itemView)
                .load(product.imgUrl)
                .into(itemImage)

            // 체크박스 or 사진 선택시 값 변경
            if (fromFragment == "addFeedFragment") { // 여러장 선택(피드 추가시)
                itemImage.setOnClickListener {
                    product.isSelected.value = !product.isSelected.value!!
                }
                checkBox.setOnClickListener {
                    product.isSelected.value = !product.isSelected.value!!
                }
            } else { // 한장 선택(유저 정보입력, 펫 정보 입력, 프로필 수정)
                itemImage.setOnClickListener {
                    checkPhoto(product)
                }
                checkBox.setOnClickListener {
                    checkPhoto(product)
                }
            }
        }
    }

    private fun checkPhoto(product: Photo) {
        if (currentSelectCnt == limitSelectCnt) { // 이미 한 개 선택되어 있다면
            selectedPhoto.isSelected.value = !selectedPhoto.isSelected.value!! // 기존에 선택된 애 해제
            selectedPhoto = product // 이번에 선택한 놈이 선택됨!
            product.isSelected.value = !product.isSelected.value!!
        } else { // 처음 선택하는 것이라면
            product.isSelected.value = !product.isSelected.value!!
            selectedPhoto = product
            currentSelectCnt++
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GalleryAdapter.GalleryHolder {
        return GalleryHolder(ItemGalleryPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        holder.apply {
            bindInfo(mainViewModel.galleryList.value!!.get(position))
        }
    }

    override fun getItemCount(): Int {
        return mainViewModel.galleryList.value!!.size
    }
}
