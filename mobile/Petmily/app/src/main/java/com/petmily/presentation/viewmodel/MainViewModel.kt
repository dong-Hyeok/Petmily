package com.petmily.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.repository.api.token.TokenService
import com.petmily.repository.dto.Photo
import com.petmily.repository.dto.TokenRequestDto
import kotlinx.coroutines.launch

private const val TAG = "Fetmily_MainViewModel"
class MainViewModel : ViewModel() {

    private val tokenService by lazy { TokenService() }

    private var fromGalleryFragment: String // GalleryFragment를 호출한 Fragment를 기록
    private var selectProfileImage: String // 갤러리에서 선택한 사진 한장

    private val _galleryList = MutableLiveData<MutableList<Photo>>()
    val galleryList: LiveData<MutableList<Photo>> get() = _galleryList

    private var _addPhotoList = MutableLiveData<MutableList<Photo>>()
    val addPhotoList: LiveData<MutableList<Photo>> get() = _addPhotoList

    private val _connectException = MutableLiveData<Boolean>()
    val connectException: LiveData<Boolean> get() = _connectException

    // 액세스 토큰 재발급 결과
    private var _newAccessToken = MutableLiveData<String>()
    val newAccessToken: LiveData<String> get() = _newAccessToken

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
//        _addPhotoList.value?.add(photo)
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

    // 통신 에러시 스낵바로 안내 메시지
    fun setConnectException() {
        _connectException.value = true
    }

    /**
     * API - 발급된 토큰 서버에 등록
     */
    fun uploadToken(token: String) {
    }

    init {
        _galleryList.value = mutableListOf()
        _addPhotoList.value = mutableListOf()
        fromGalleryFragment = ""
        selectProfileImage = ""
    }

    fun initAddPhotoList() { _addPhotoList = MutableLiveData<MutableList<Photo>>() }
}
