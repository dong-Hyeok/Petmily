package com.petmily.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.repository.api.infoInput.pet.PetInfoInputService
import com.petmily.repository.api.walk.WalkService
import com.petmily.repository.dto.Pet
import com.petmily.repository.dto.WalkInfoResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

private const val TAG = "Fetmily_PetViewModel"
class PetViewModel : ViewModel() {
    private val petInfoInputService: PetInfoInputService by lazy { PetInfoInputService() }

    // 상대방 mypage에서 왔는지 판별 Email값 저장
    var fromPetInfoEmail = ""

    // 반려동물 상세보기 (PetInfoFragment) data 저장
    var selectPetInfo = Pet()

    // 반려동물 정보 수정 | 삭제 판별
    var fromPetInfoInputFragment = ""

    // 반려동물 정보 등록 통신 결과
    private var _isPetSaved = MutableLiveData<Boolean>()
    val isPetSaved: LiveData<Boolean>
        get() = _isPetSaved

    // 반려동물 정보 수정 통신 결과
    private var _isPetUpdated = MutableLiveData<Boolean>()
    val isPetUpdated: LiveData<Boolean>
        get() = _isPetUpdated

    // 반려동물 정보 삭제 통신 결과
    private var _isPetDeleted = MutableLiveData<Boolean>()
    val isPetDeleted: LiveData<Boolean>
        get() = _isPetDeleted

    /**
     *  API - 반려동물 정보 저장 통신
     */
    fun savePetInfo(file: MultipartBody.Part?, pet: Pet) {
        Log.d(TAG, "savePetInfo : 반려동물 정보 저장 file: $file / pet: $pet")
        viewModelScope.launch {
            _isPetSaved.value = petInfoInputService.petSave(file, pet)
            Log.d(TAG, "savePetInfo 결과: ${_isPetSaved.value}")
        }
    }

    /**
     *  API - 반려동물 정보 수정 통신
     */
    fun updatePetInfo(petId: Long, file: MultipartBody.Part?, pet: Pet, mainViewModel: MainViewModel) {
        Log.d(TAG, "updatePetInfo : 반려동물 정보 수정 petId: $petId / file: $file / pet: $pet")
        viewModelScope.launch {
            _isPetUpdated.value = petInfoInputService.petUpdate(petId, file, pet)
            Log.d(TAG, "updatePetInfo 결과: ${_isPetUpdated.value }")
        }
    }

    /**
     *  API - 반려동물 정보 삭제 통신
     */
    fun deletePetInfo(petId: Long, mainViewModel: MainViewModel) {
        Log.d(TAG, "deletePetInfo: 반려동물 정보 삭제")
        viewModelScope.launch {
            _isPetDeleted.value = petInfoInputService.petDelete(petId)
        }
    }

    fun initIsPetSaved() { _isPetSaved = MutableLiveData<Boolean>() }
    fun initIsPetUpdated() { _isPetUpdated = MutableLiveData<Boolean>() }
    fun initIsPetDeleted() { _isPetDeleted = MutableLiveData<Boolean>() }

    // ------------------------------------------------------------------------------------------------------------------------
    // Walk
    // ------------------------------------------------------------------------------------------------------------------------

    private val walkService by lazy { WalkService() }

    // 반려동물 산책 저장 통신 결과
    private var _isWalkSaved = MutableLiveData<Boolean>()
    val isWalkSaved: LiveData<Boolean> get() = _isWalkSaved

    // 반려동물 산책 기록
    private var _walkInfoList = MutableLiveData<List<WalkInfoResponse>>()
    val walkInfoList: LiveData<List<WalkInfoResponse>> get() = _walkInfoList

    // 산책용 내 반려동물 리스트
    var myPetList: List<Pet> = listOf()

    // 산책에서 선택한 동물
    var walkingPet = Pet()

    /**
     * API - 산책 정보 저장
     */
    fun saveWalk(petId: Long, walkDate: String, walkDistance: Int, walkSpend: Int) {
        Log.d(TAG, "saveWalk: 산책 정보 저장")
        viewModelScope.launch {
            _isWalkSaved.value = walkService.saveWalk(petId, walkDate, walkDistance, walkSpend)
        }
    }

    /**
     * API - 사용자의 반려동물 산책 정보 전체 조회
     */
    fun getUserPetWalkInfo(userEmail: String) {
        Log.d(TAG, "getUserPetWalkInfo: 산책 정보 전체 조회")
        viewModelScope.launch {
            _walkInfoList.value = walkService.userPetWalkInfo(userEmail)
        }
    }

    fun initIsWalkSaved() { _isWalkSaved = MutableLiveData<Boolean>() }
}
