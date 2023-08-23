package com.petmily.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.config.ApplicationClass
import com.petmily.repository.api.notification.NotificationService
import com.petmily.repository.api.shop.ShopService
import com.petmily.repository.api.token.TokenService
import com.petmily.repository.dto.FcmToken
import com.petmily.repository.dto.Photo
import com.petmily.repository.dto.ResponseNotification
import com.petmily.repository.dto.TokenRequestDto
import com.petmily.repository.dto.UserLoginInfoDto
import kotlinx.coroutines.launch

private const val TAG = "Fetmily_MainViewModel"
class MainViewModel : ViewModel() {

    private val tokenService by lazy { TokenService() }
    private val shopService by lazy { ShopService() }
    private val notificationService by lazy { NotificationService() }

    private var fromGalleryFragment: String // GalleryFragment를 호출한 Fragment를 기록
    private var selectProfileImage: String // 갤러리에서 선택한 사진 한장

    private val _galleryList = MutableLiveData<MutableList<Photo>>()
    val galleryList: LiveData<MutableList<Photo>>
        get() = _galleryList

    private var _addPhotoList = MutableLiveData<MutableList<Photo>>()
    val addPhotoList: LiveData<MutableList<Photo>>
        get() = _addPhotoList

    // 액세스 토큰 재발급 결과
    private var _newAccessToken = MutableLiveData<String>()
    val newAccessToken: LiveData<String>
        get() = _newAccessToken

    /**
     * API - 액세스 토큰 재발급
     */
    fun refreshAccessToken(tokenRequestDto: TokenRequestDto) {
        viewModelScope.launch {
            _newAccessToken.value = tokenService.refreshAccessToken(tokenRequestDto)
        }
    }

    // GalleryFragment에서 선택된 사진 add
    fun addToAddPhotoList(photo: Photo) {
        val tmpList = mutableListOf<Photo>()
        tmpList.add(photo)
        _addPhotoList.value =
            if (_addPhotoList.value == null) {
                tmpList
            } else {
                (_addPhotoList.value!! + tmpList).toMutableList()
            }

        Log.d(TAG, "addToAddPhotoList: ${_addPhotoList.value}")
    }

    // GalleryFragment에서 선택된 사진 초기화
    fun clearAddPhotoList() {
        _addPhotoList.value?.clear()
        _addPhotoList.value = _addPhotoList.value
    }

    // 핸드폰 갤러리에서 불러온 사진을 초기화
    fun emptyGalleryList() {
        _galleryList.value!!.clear()
    }

    // 핸드폰 갤러리에서 불러온 사진을 add
    fun addToGalleryList(photo: Photo) {
        _galleryList.value?.add(photo)
        _galleryList.value = _galleryList.value
    }

    fun getSelectProfileImage(): String {
        return selectProfileImage
    }
    fun setSelectProfileImage(selectProfileImage: String) {
        this.selectProfileImage = selectProfileImage
    }

    fun getFromGalleryFragment(): String {
        return fromGalleryFragment
    }
    fun setFromGalleryFragment(fragmentName: String) {
        fromGalleryFragment = fragmentName
    }

    /**
     * ---------------------------------------------------------------------
     *                               출석 체크
     * ---------------------------------------------------------------------
     */

    // 출석 체크 결과
    private var _resultAttendance = MutableLiveData<Boolean>()
    val resultAttendance: LiveData<Boolean>
        get() = _resultAttendance

    /**
     * API - 출석 포인트 ++
     * 통신결과 성공이면 sharePreferences에 setAttendanceTime()에 현재 시간으로 업데이트
     * shopService에 구현
     */
    fun requestAttendance() {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")
            val result = shopService.requestAttendance(UserLoginInfoDto(userEmail = userEmail!!))
            _resultAttendance.postValue(result)
        }
    }
    
    fun initResultAttendance() { _resultAttendance = MutableLiveData<Boolean>() }
    
    /**
     * ---------------------------------------------------------------------
     *                               알림
     * ---------------------------------------------------------------------
     */

    private val _resultNotification = MutableLiveData<MutableList<ResponseNotification>>()
    val resultNotification: LiveData<MutableList<ResponseNotification>>
        get() = _resultNotification
    
    // FCM 토큰
    var fcmToken = ""
    
    /**
     * API - 발급된 토큰 서버에 등록
     */
    fun uploadFcmToken(fcmToken: String) {
        this.fcmToken = fcmToken
    }

    /**
     * FCM 토근 저장
     */
    fun requstSaveToken() {
        Log.d(TAG, "requstSaveToken: 토큰 저장 요청")
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")!!
            val result = notificationService.requstSaveToken(
                FcmToken(
                    userEmail = userEmail,
                    fcmToken = fcmToken,
                ),
            )
            Log.d(TAG, "requstSaveToken: $result")
        }
    }
    
    /**
     * API - notification list
     */
    fun requestNotification() {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")!!
            var result = mutableListOf<ResponseNotification>()
            result = notificationService.requestNotification(userEmail)
            _resultNotification.postValue(result)
        }
    }

    init {
        _galleryList.value = mutableListOf()
        _addPhotoList.value = mutableListOf()
        fromGalleryFragment = ""
        selectProfileImage = ""
    }

    fun initAddPhotoList() { _addPhotoList = MutableLiveData<MutableList<Photo>>() }
}
