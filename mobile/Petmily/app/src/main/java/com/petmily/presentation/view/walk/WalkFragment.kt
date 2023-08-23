package com.petmily.presentation.view.walk

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentWalkBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.PetViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import com.petmily.repository.dto.Pet
import com.petmily.repository.dto.WalkInfoResponse
import com.petmily.util.StringFormatUtil
import com.petmily.util.WalkWorker

private const val TAG = "Fetmily_WalkFragment"
class WalkFragment : BaseFragment<FragmentWalkBinding>(FragmentWalkBinding::bind, R.layout.fragment_walk) {

    private val mainActivity by lazy { context as MainActivity }

    private val userViewModel: UserViewModel by activityViewModels()
    private val petViewModel: PetViewModel by activityViewModels()

    private lateinit var walkListAdapter: WalkListAdapter

    private lateinit var workManager: WorkManager
    private lateinit var workRequest: WorkRequest

    private var isPermissionChecked = false

    private val locationManager by lazy {
        // ~~Manager는 getSystemService로 호출
        mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    companion object {
        var walkDist = 0F
        var walkTime = 0
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviInVisible()
        initAdapter()
        initView()
        initClick()
        initData()
        initObserver()
        checkPermission()
    }

    private fun initAdapter() {
        walkListAdapter = WalkListAdapter()
        binding.rcvWalkList.apply {
            adapter = walkListAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun initView() = with(binding) {
        if (ApplicationClass.sharedPreferences.isWalking()) {
            btnWalkStart.text = "산책 종료!"
            tilWalkAnimal.isEnabled = false
            actWalkAnimal.setText(ApplicationClass.sharedPreferences.getString("petName"))
            petViewModel.walkingPet = Pet(
                petId = ApplicationClass.sharedPreferences.getLong("petId"),
                petName = ApplicationClass.sharedPreferences.getString("petName") ?: "",
            )
        } else {
            btnWalkStart.text = "산책 시작!"
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initClick() = with(binding) {
        // 산책 시작/종료 버튼
        btnWalkStart.setOnClickListener {
            if (ApplicationClass.sharedPreferences.isWalking()) {
                // 산책중일 경우 산책 종료
                stopWalk()
            } else if (isPermissionChecked && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // 산책중이지 않으며 권한이 적용되어 있을 경우
                if (petViewModel.walkingPet.petId == 0L) {
                    // 선택된 반려동물이 없을 경우
                    mainActivity.showSnackbar("산책할 반려동물을 선택해주세요.")
                } else {
                    // 산책 시작
                    startWalk()
                }
            } else {
                // 권한 재확인
                checkPermission()
            }
        }

        // 산책할 반려동물 선택 Dropdown
        actWalkAnimal.setOnItemClickListener { _, _, idx, _ ->
            petViewModel.walkingPet = petViewModel.myPetList[idx]
        }

        // 뒤로가기 버튼
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initData() {
        userViewModel.selectedUserLoginInfoDto.userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: ""
        userViewModel.requestMypageInfo()

        petViewModel.getUserPetWalkInfo(ApplicationClass.sharedPreferences.getString("userEmail") ?: "")
    }

    private fun initObserver() {
        userViewModel.mypageInfo.observe(viewLifecycleOwner) {
            petViewModel.myPetList = it.userPets

            // 산책할 반려동물 선택 Dropdown
            binding.actWalkAnimal.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_email, it.userPets.map { it.petName }))

            if (it.userPets.isEmpty()) {
                binding.tilWalkAnimal.apply {
                    hint = "마이페이지에서 반려동물을 등록해주세요."
                    isEnabled = false
                }
                binding.btnWalkStart.apply {
                    isEnabled = false
                }
            }
        }

        // 반려동물 산책 정보 저장 결과
        petViewModel.initIsWalkSaved()
        petViewModel.isWalkSaved.observe(viewLifecycleOwner) {
            if (it) {
                petViewModel.getUserPetWalkInfo(ApplicationClass.sharedPreferences.getString("userEmail") ?: "")
            }
        }

        // 산책 정보 전체 조회
        petViewModel.walkInfoList.observe(viewLifecycleOwner) {
            initCalendar(it)
        }
    }

    private fun initCalendar(walkInfoResponseList: List<WalkInfoResponse>) = with(binding) {
        // 달력 날짜 선택
        cvWalkCalendar.setOnDateChangeListener { _, year, month, day ->
            Log.d(TAG, "캘린더 뷰에서 선택한 날짜: $year-$month-$day")

            // 선택한 날짜에 따라 어댑터 갱신
            walkListAdapter.setWalkInfoList(
                walkInfoResponseList.map { walkInfoResponse ->
                    walkInfoResponse.walks.map {
                        it.apply { pet = walkInfoResponse.pet }
                    }
                }
                    .flatten()
                    .filter {
                        year == it.walkDate.substring(0..3).toInt() &&
                            month + 1 == it.walkDate.substring(5..6).toInt() &&
                            day == it.walkDate.substring(8..9).toInt()
                    }
                    .sortedBy { it.walkId },
            )
        }
    }

    // 권한 체크
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermission() {
        workManager = WorkManager.getInstance(mainActivity)
        workRequest = OneTimeWorkRequestBuilder<WalkWorker>().build()

        val permissionListener = object : PermissionListener {
            // 권한 얻기에 성공했을 때 동작 처리
            override fun onPermissionGranted() {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

                isPermissionChecked = true
            }

            // 권한 얻기에 실패했을 때 동작 처리
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                isPermissionChecked = false
                mainActivity.showSnackbar("위치 권한이 거부되었습니다.")
            }
        }
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("권한 -> 위치 -> 항상허용 권한을 부여해야만 사용이 가능합니다.")
            // 필요한 권한 설정
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION, // 없으면 앱 내려가면 위치 정보 읽지 않음
//                Manifest.permission.POST_NOTIFICATIONS, // targetSdk 33에서는 적용 필요
            )
            .check()
    }

    // 산책 종료
    private fun stopWalk() {
        mainActivity.showSnackbar("산책을 종료합니다")

        ApplicationClass.sharedPreferences.stopWalk()

        binding.apply {
            btnWalkStart.text = "산책 시작!"
            tilWalkAnimal.isEnabled = true
        }

        workManager.cancelAllWork()
    }

    @SuppressLint("RestrictedApi")
    private fun startWalk() {
        mainActivity.showSnackbar("${binding.actWalkAnimal.text}와 함께 산책을 시작합니다!")

        ApplicationClass.sharedPreferences.startWalk(petViewModel.walkingPet)

        binding.apply {
            btnWalkStart.text = "산책 종료!"
            tilWalkAnimal.isEnabled = false
        }

        walkDist = 0f
        walkTime = 0

        workManager = WorkManager.getInstance(mainActivity)
        workRequest = OneTimeWorkRequestBuilder<WalkWorker>().build()

        workManager.enqueue(workRequest)

        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(
                mainActivity,
            ) { workInfo ->
                if (workInfo != null) {
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        // Work completed successfully
                        Log.d(TAG, "onCreate: success")
                    } else if (workInfo.state == WorkInfo.State.FAILED) {
                        // Work failed
                        Log.d(TAG, "onCreate: fail")
                    } else if (workInfo.state == WorkInfo.State.CANCELLED) {
                        // Work cancelled
                        // WalkWorker는 Notification을 위해 무한반복하게 두고 WalkFragment에서 취소하면
                        // companion object에 저장해둔 결과 호출하여 처리
                        Log.d(TAG, "산책 종료 - 거리: $walkDist / 시간: $walkTime")
                        petViewModel.saveWalk(
                            petViewModel.walkingPet.petId,
                            StringFormatUtil.currentTimeToTimeStamp(),
                            walkDist.toInt(),
                            walkTime,
                        )
                    }
                }
            }
    }
}
