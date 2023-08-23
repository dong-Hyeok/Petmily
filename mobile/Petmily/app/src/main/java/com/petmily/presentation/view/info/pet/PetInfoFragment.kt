package com.petmily.presentation.view.info.pet

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentPetInfoBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.PetViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import com.petmily.repository.dto.Pet

class PetInfoFragment :
    BaseFragment<FragmentPetInfoBinding>(FragmentPetInfoBinding::bind, R.layout.fragment_pet_info) {

    private lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val petViewModel: PetViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviInVisible()
        initBackPressEvent()
        initPetInfo()
        initButton()
        initObserve()
    }

    private fun initBackPressEvent() {
        // 핸드폰 기기 back버튼
        mainActivity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goBack()
                }
            },
        )
    }

    /**
     * Pet 정보 세팅
     */
    private fun initPetInfo() = with(binding) {
        petViewModel.selectPetInfo.apply {
            if (petImg.isNullOrBlank()) {
                Glide.with(mainActivity)
                    .load(R.drawable.ic_pet_background)
                    .into(ivPetInfo)
            } else {
                Glide.with(mainActivity)
                    .load(petImg)
                    .into(ivPetInfo)
            }

            if (petViewModel.fromPetInfoEmail != ApplicationClass.sharedPreferences.getString("userEmail")) { // 상대방 마이페이지에서 온 것이라면 수정, 삭제 버튼 Invisible
                btnPetInfoDelete.visibility = View.INVISIBLE
                btnPetInfoModify.visibility = View.INVISIBLE
            }

            tvPetInfoName.text = petName
            tvPetInfoGender.text = if (petGender == "male") "수컷" else "암컷"
            tvPetInfoSpecies.text = speciesName
            tvPetInfoBirthContents.text = petBirth
            tvPetInfoIntroContents.text = petInfo
        }
    }

    private fun initButton() = with(binding) {
        ivBack.setOnClickListener {
            goBack()
        }

        btnPetInfoModify.setOnClickListener {
            petViewModel.fromPetInfoInputFragment = "PetInfoFragment"
            mainActivity.changeFragment("petInfoInput")
        }

        btnPetInfoDelete.setOnClickListener {
            petViewModel.deletePetInfo(petViewModel.selectPetInfo.petId, mainViewModel)
        }
    }

    private fun initObserve() = with(petViewModel) {
        initIsPetDeleted()

        // 펫 정보 삭제
        isPetDeleted.observe(viewLifecycleOwner) {
            if (it) { // pet 정보가 삭제 성공
                userViewModel.requestMypageInfo()
                mainActivity.showSnackbar("성공적으로 삭제 되었습니다.")
                parentFragmentManager.popBackStack()
            } else {
                mainActivity.showSnackbar("삭제 실패하였습니다.")
            }
        }
    }

    private fun goBack() {
        petViewModel.selectPetInfo = Pet()

        // 상대방 마이페이지에서 온 것이라면
        userViewModel.selectedUserLoginInfoDto.userEmail = petViewModel.fromPetInfoEmail
        petViewModel.fromPetInfoEmail = ""

        parentFragmentManager.popBackStack()
    }
}
