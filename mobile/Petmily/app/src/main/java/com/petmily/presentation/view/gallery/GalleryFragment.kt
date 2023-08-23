package com.petmily.presentation.view.gallery

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentGalleryBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.MainViewModel

const val TAG = "petmily_GalleryFragment"
class GalleryFragment :
    BaseFragment<FragmentGalleryBinding>(FragmentGalleryBinding::bind, R.layout.fragment_gallery) {

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var mainActivity: MainActivity
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviInVisible()

        initRecyclerView()
        initButton()
    }

    private fun initRecyclerView() = with(binding) {
        galleryAdapter = GalleryAdapter(viewLifecycleOwner, mainViewModel)

        rcvGallery.apply {
            layoutManager = GridLayoutManager(mainActivity, 3)
            adapter = galleryAdapter
        }
    }

    private fun initButton() = with(binding) {
        // 사진 선택 완료 버튼
        btnGalleryComplete.setOnClickListener {
            Log.d(TAG, "initButton: ${mainViewModel.getFromGalleryFragment()}")

            when (mainViewModel.getFromGalleryFragment()) {
                "userInfoInput" -> {
                    getPhoto()
                }

                "petInfoInput" -> {
                    getPhoto()
                }

                "addFeedFragment" -> {
                    getPhotos()
                }
            }
            parentFragmentManager.popBackStack()
        }

        // 핸드폰 기기 back버튼
        mainActivity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            },
        )
    }

    /**
     *  갤러리에서 선택한 사진 한장
     */
    private fun getPhoto() {
        for (photo in mainViewModel.galleryList.value!!) { // 갤러리에서 선택한 사진을 저장
            if (photo.isSelected.value!!) {
                mainViewModel.setSelectProfileImage(photo.imgUrl)
                Log.d(TAG, "getPhoto: ${mainViewModel.getSelectProfileImage()}")
                break
            }
        }
    }

    /**
     *  갤러리에서 선택한 사진 n장
     */
    private fun getPhotos() = with(mainViewModel) {
        clearAddPhotoList()
        for (photo in galleryList.value!!) { // 갤러리에서 선택한 사진을 저장
            if (photo.isSelected.value!!) addToAddPhotoList(photo)
        }
    }
}
