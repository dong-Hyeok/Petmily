package com.petmily.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.repository.api.certification.join.JoinService
import com.petmily.repository.api.certification.login.LoginService
import com.petmily.repository.dto.LoginResponse
import com.petmily.repository.dto.Pet
import kotlinx.coroutines.launch
import java.net.ConnectException

private const val TAG = "Fetmily_UserViewModel"

class UserViewModel : ViewModel() {
    private val loginService: LoginService by lazy { LoginService() }
    private val joinService: JoinService by lazy { JoinService() }

    // 로그인 토큰
    private val _user = MutableLiveData<LoginResponse>()
    val user: LiveData<LoginResponse>
        get() = _user

    // 이메일 인증 코드
    private val _emailCode = MutableLiveData<String>()
    val emailCode: LiveData<String>
        get() = _emailCode

    // 이메일 인증 코드 확인
    private val _isEmailCodeChecked = MutableLiveData<Boolean>()
    val isEmailCodeChecked: LiveData<Boolean>
        get() = _isEmailCodeChecked

    // 회원가입
    private val _isJoined = MutableLiveData<Boolean>()
    val isJoined: LiveData<Boolean>
        get() = _isJoined

    // Pet 정보 입력 List
    var petInfoList: MutableList<Pet> = mutableListOf()

    fun addPetInfo(pet: Pet) {
        petInfoList.add(pet)
    }

    fun getPetInfo(): MutableList<Pet> {
        return petInfoList
    }

    fun login(email: String, pwd: String, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            try {
                _user.value = loginService.login(email, pwd)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    fun sendEmailAuth(userEmail: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "sendEmailAuth: 이메일 인증 코드 요청 / userEmail: $userEmail")
        viewModelScope.launch {
            try {
                _emailCode.value = joinService.sendEmailCode(userEmail)
                Log.d(TAG, "sendEmailAuth: 인증 코드: ${_emailCode.value}")
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    fun checkEmailCode(code: String, userEmail: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "checkEmailCode: 이메일 인증 요청 / userEmail: $userEmail, code: ${_emailCode.value}")
        viewModelScope.launch {
            try {
                _isEmailCodeChecked.value = joinService.checkEmailCode(code, userEmail)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    fun join(userEmail: String, userPw: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "join: 회원가입 / userEmail: $userEmail, userPw: $userPw")
        viewModelScope.launch {
            try {
                _isJoined.value = joinService.join(userEmail, userPw)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }
}
