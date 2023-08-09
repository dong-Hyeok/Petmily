package com.petmily.presentation.view.info.user

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentUserInfoInputBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import com.petmily.repository.dto.User
import com.petmily.util.CheckPermission
import com.petmily.util.GalleryUtil
import com.petmily.util.UploadUtil

private const val TAG = "Petmily_UserInfoInputFragment"

@SuppressLint("LongLogTag")
class UserInfoInputFragment : BaseFragment<FragmentUserInfoInputBinding>(FragmentUserInfoInputBinding::bind, R.layout.fragment_user_info_input) {

    private lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var galleryUtil: GalleryUtil
    private lateinit var uploadUtil: UploadUtil
    private lateinit var checkPermission: CheckPermission

    private var nickNameDupCheck = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        galleryUtil = GalleryUtil()
        checkPermission = CheckPermission()
        uploadUtil = UploadUtil()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initView()
        initEditText()
        initBtn()
        initObserve()
    }

    private fun init() = with(binding) {
        // 유저 닉네임이 null이면 back 버튼을 제거
        if (ApplicationClass.sharedPreferences.getString("userNickname").isNullOrBlank()) {
            ivBack.visibility = View.GONE
        }

        // 입력 상태
        etNickname.setText(userViewModel.getUserInfoInputNickName())
        actFavorAnimal.setText(userViewModel.getUserInfoInputPet())

        mainViewModel.setFromGalleryFragment("userInfoInput")
        Log.d(TAG, "mainViewModel: ${mainViewModel.getFromGalleryFragment()}")
    }

    private fun initView() = with(binding) {
        // 프로필 사진 setting
        // TODO: Room에 userProfileImage 갱신 & 서버에 userProfileImage 갱신

        // userInfoInput의 프레그먼트 상태일 때 ->
        if (mainViewModel.getFromGalleryFragment() == "userInfoInput") {
            Glide.with(mainActivity)
                .load(mainViewModel.getSelectProfileImage()) // 내가 선택한 사진이 우선 들어가가있음
                .circleCrop()
                .into(ivUserImage)
        }

        // 프로필 사진 view
        ivUserImage.setOnClickListener {
            if (checkPermission.requestStoragePermission()) { // 갤러리 접근 권한 체크
                if (galleryUtil.getImages(mainActivity, mainViewModel)) { // 갤러리 이미지를 모두 로드 했다면
                    userViewModel.setUserInfoInputSave(etNickname.text.toString(), actFavorAnimal.text.toString())
                    mainActivity.changeFragment("gallery")
                }
            }

            // 선호 반려동물 선택
            actFavorAnimal.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_email, species))
        }

        // 뒤로 가기 (마이페이지)
        ivBack.setOnClickListener {
            userViewModel.clearUserInfo()
            mainViewModel.setSelectProfileImage("") // 선택 이미지 초기화
            mainActivity.changeFragment("my page")
        }
    }

    private fun initEditText() = with(binding) {
        // 닉네임 입력
        etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (tilId.isErrorEnabled) tilId.isErrorEnabled = false
                nickNameDupCheck = false
            }
        })

        // 선호 반려동물
        actFavorAnimal.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_email, species))
    }

    private fun initBtn() = with(binding) {
        // 닉네임 중복확인
        btnNicknameDupConfirm.setOnClickListener {
            if (etNickname.text.isNullOrBlank()) tilId.error = getString(R.string.userinfoinput_error_nickname)

            if (tilId.error.isNullOrBlank()) { // 에러가 없으면(== 닉네임 입력 헀으면) -> 닉네임 중복체크
                userViewModel.requestDupNickNameCheck(etNickname.text.toString(), mainViewModel)
            }
        }

        // 완료 -> user정보 업데이트
        btnConfirm.setOnClickListener {
            if (nickNameDupCheck) {
                // 이미지 변환
                Log.d(TAG, "userInfoInput select Image: ${mainViewModel.getSelectProfileImage()}")
                val image =
                    if (mainViewModel.getSelectProfileImage().isNullOrBlank()) {
                        null
                    } else {
                        uploadUtil.createMultipartFromUri(mainActivity, "file", mainViewModel.getSelectProfileImage())
                    }
                
                // ViewModel에 유저 정보 입력 call
                userViewModel.requestEditMyPage(
                    etNickname.text.toString(),
                    actFavorAnimal.text.toString(),
                    image,
                    mainViewModel,
                )

                userViewModel.clearUserInfo()
            } else {
                mainActivity.showSnackbar("닉네임 중복 체크가 필요합니다.")
            }
        }
    }

    private fun initObserve() = with(userViewModel) {
        initIsCheckNickName()
        isCheckNickName.observe(viewLifecycleOwner) {
            nickNameDupCheck = it
        }
    
        // 유저정보 등록 결과
        initEditMyPageResult()
        editMyPageResult.observe(viewLifecycleOwner) {
            if (it.userId == 0L) {
                // 유저 추가정보 등록 실패
                mainActivity.showSnackbar("정보 등록에 실패하였습니다.")
            } else {
                // 유저 추가정보 등록 성공
                mainActivity.showSnackbar("성공적으로 등록되었습니다.")
                
                // 이메일, 프로필이미지, 닉네임 갱신
                ApplicationClass.sharedPreferences.addUser(it)
                
                mainViewModel.setSelectProfileImage("")
                mainActivity.initSetting()
            }
        }
        
        // 닉네임 중복체크 결과 
        isCheckNickName.observe(viewLifecycleOwner) {
            if (it) {
                mainActivity.showSnackbar("사용가능한 닉네임 입니다.")
            } else {
                mainActivity.showSnackbar("사용할 수 없는 닉네임 입니다.")
            }
        }
    }

    companion object {
        val species = arrayOf(
            "강아지",
            "고양이",
            "기타 동물",
        )
    }
}
