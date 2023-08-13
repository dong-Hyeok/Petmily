package com.petmily.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.config.ApplicationClass
import com.petmily.repository.api.certification.join.JoinService
import com.petmily.repository.api.certification.login.LoginService
import com.petmily.repository.api.certification.password.PasswordService
import com.petmily.repository.api.infoInput.user.UserInfoInputService
import com.petmily.repository.api.mypage.MypageService
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.Curation
import com.petmily.repository.dto.EditUserInfoResponse
import com.petmily.repository.dto.LoginResponse
import com.petmily.repository.dto.MypageInfo
import com.petmily.repository.dto.Pet
import com.petmily.repository.dto.User
import com.petmily.repository.dto.UserInfo
import com.petmily.repository.dto.UserProfileResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.net.ConnectException

private const val TAG = "Petmily_UserViewModel"

class UserViewModel : ViewModel() {
    private val loginService: LoginService by lazy { LoginService() }
    private val joinService: JoinService by lazy { JoinService() }
    private val passwordService: PasswordService by lazy { PasswordService() }
    private val userInfoInputService: UserInfoInputService by lazy { UserInfoInputService() }
    private val mypageService: MypageService by lazy { MypageService() }

    // 이메일 인증 수신 결과 저장
    var checkSuccessEmail = ""

    // userInfo 입력 상태 유지
    var userInfoNickName = ""
    var userInfoPet = ""

    // userInfoInput으로 이동시 어디서 왔는지
    var fromUserInfoInput = ""

    // 로그인 토큰
    private var _user = MutableLiveData<LoginResponse>()
    val user: LiveData<LoginResponse>
        get() = _user

    // 회원가입 이메일 인증 코드
    private var _joinEmailCode = MutableLiveData<String>()
    val joinEmailCode: LiveData<String>
        get() = _joinEmailCode

    // 회원가입 이메일 인증 코드 확인
    private var _isJoinEmailCodeChecked = MutableLiveData<Boolean>()
    val isJoinEmailCodeChecked: LiveData<Boolean>
        get() = _isJoinEmailCodeChecked

    // 회원가입
    private var _isJoined = MutableLiveData<Boolean>()
    val isJoined: LiveData<Boolean>
        get() = _isJoined

    // 비밀번호 재설정 - 이메일 인증 상태
    private var _pwdEmailCode = MutableLiveData<String>()
    val pwdEmailCode: LiveData<String>
        get() = _pwdEmailCode

    // 비밀번호 재설정 - 인증코드 확인
    private var _isPwdEmailCodeChecked = MutableLiveData<Boolean>()
    val isPwdEmailCodeChecked: LiveData<Boolean>
        get() = _isPwdEmailCodeChecked

    // 비밀번호 재설정 - 최종 완료 상태
    private var _isChangePassword = MutableLiveData<Boolean>()
    val isChangepPassword: LiveData<Boolean>
        get() = _isChangePassword

    // 유저정보입력 - 닉네임 중복 체크
    private var _isCheckNickName = MutableLiveData<Boolean>()
    val isCheckNickName: LiveData<Boolean>
        get() = _isCheckNickName

    // 유저정보입력 - 정보 등록
    private var _editMyPageResult = MutableLiveData<EditUserInfoResponse>()
    val editMyPageResult: LiveData<EditUserInfoResponse>
        get() = _editMyPageResult

    // Pet 정보 입력 List
    var petInfoList: MutableList<Pet> = mutableListOf()

    fun addPetInfo(pet: Pet) {
        petInfoList.add(pet)
    }

    fun getPetInfo(): MutableList<Pet> {
        return petInfoList
    }

    /**
     * API - 로그인 요청
     */
    fun login(email: String, pwd: String, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            try {
                _user.value = loginService.login(email, pwd)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     *  API - 비밀번호 재설정 - 이메일 인증 코드 요청
     */
    fun sendPassEmailAuth(userEmail: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "sendEmailAuth: 이메일 인증 코드 요청 / userEmail: $userEmail")
        viewModelScope.launch {
            try {
                _pwdEmailCode.value = passwordService.requestEmailCode(userEmail)
                Log.d(TAG, "sendEmailAuth: 인증 코드: ${_pwdEmailCode.value}")
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     *  API - 비밀번호 재설정 - 이메일 인증 완료 요청
     */
    fun checkPasswordEmailCode(code: String, userEmail: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "checkEmailCode: 이메일 인증 요청 / userEmail: $userEmail, code: ${_pwdEmailCode.value}")
        viewModelScope.launch {
            try {
                _isPwdEmailCodeChecked.value = passwordService.checkEmailCode(code, userEmail)
                if (isPwdEmailCodeChecked.value!!) checkSuccessEmail = userEmail
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 비밀번호 재설정 - 비밀번호 재설정 완료 버튼(변경된 비밀번호 반환 받음)
     */
    fun changePassword(userEmail: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "Password: userEmail: $userEmail")
        viewModelScope.launch {
            try {
                _isChangePassword.value = passwordService.changePassRequest(userEmail)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 회원가입 - 이메일 인증 코드 요청
     */
    fun sendJoinEmailAuth(userEmail: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "sendEmailAuth: 이메일 인증 코드 요청 / userEmail: $userEmail")
        viewModelScope.launch {
            try {
                _joinEmailCode.value = joinService.requestEmailCode(userEmail) // 서비스 요청
                Log.d(TAG, "sendEmailAuth: 인증 코드: ${_joinEmailCode.value}")
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 회원가입 - 이메일 인증 완료 요청
     */
    fun checkJoinEmailCode(code: String, userEmail: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "checkEmailCode: 이메일 인증 요청 / userEmail: $userEmail, code: ${_joinEmailCode.value}")
        viewModelScope.launch {
            try {
                _isJoinEmailCodeChecked.value = joinService.checkEmailCode(code, userEmail)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 회원가입 - 회원가입 완료 버튼
     */
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

    /**
     * API - 유저 정보 편집 (이미지 파일 null 가능)
     */
    fun requestEditMyPage(userNickName: String, userLikePet: String, file: MultipartBody.Part?, mainViewModel: MainViewModel) {
        Log.d(TAG, "userEmail: ${ ApplicationClass.sharedPreferences.getString("userEmail")} , userInfo: nickName: $userNickName , likePet: $userLikePet , imageFile: $file")
        viewModelScope.launch {
            try {
                val user = User(
                    ApplicationClass.sharedPreferences.getString("userEmail")!!,
                    userNickName,
                    userLikePet,
                )
                _editMyPageResult.value = userInfoInputService.requestEditMyPage(UserInfo(user, file))
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 유저 정보 입력 전 NickName 중복체크
     */
    fun requestDupNickNameCheck(userNickName: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "requestDupNickNameCheck: nickName: $userNickName")
        viewModelScope.launch {
            try {
                val user = User(userNickName)
                _isCheckNickName.value = userInfoInputService.requestDupNickNameCheck(user)
                Log.d(TAG, "requestDupNickNameCheck: result: ${_isCheckNickName.value}")
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * 유저 정보 입력 상태 저장
     */
    fun setUserInfoInputSave(nickName: String, pet: String) {
        userInfoNickName = nickName
        userInfoPet = pet
    }

    /**
     * 유저 정보 입력 상태 얻기
     */
    fun getUserInfoInputNickName(): String {
        return userInfoNickName
    }
    fun getUserInfoInputPet(): String {
        return userInfoPet
    }

    /**
     * 유저 정보 입력 상태 초기화
     */
    fun clearUserInfo() {
        userInfoNickName = ""
        userInfoPet = ""
    }

    fun initUser() { _user = MutableLiveData<LoginResponse>() }
    fun initJoinEmailCode() { _joinEmailCode = MutableLiveData<String>() }
    fun initIsJoinEmailCodeChecked() { _isJoinEmailCodeChecked = MutableLiveData<Boolean>() }
    fun initIsJoined() { _isJoined = MutableLiveData<Boolean>() }
    fun initPwdEmailCode() { _pwdEmailCode = MutableLiveData<String>() }
    fun initIsPwdEmailCodeChecked() { _isPwdEmailCodeChecked = MutableLiveData<Boolean>() }
    fun initIsChangePassword() { _isChangePassword = MutableLiveData<Boolean>() }
    fun initIsCheckNickName() { _isCheckNickName = MutableLiveData<Boolean>() }
    fun initEditMyPageResult() { _editMyPageResult = MutableLiveData<EditUserInfoResponse>() }

    // ---------------------------------------------------------------------------------------------
    //  MYPage
    // ---------------------------------------------------------------------------------------------

    private val _mypageInfo = MutableLiveData<MypageInfo>()
    val mypageInfo: LiveData<MypageInfo>
        get() = _mypageInfo

    // 좋아요 누른 리스트
    private var _likeBoardList = MutableLiveData<List<Board>>()
    val likeBoardList: LiveData<List<Board>> get() = _likeBoardList

    // 팔로잉 리스트
    private var _followingList = MutableLiveData<List<UserProfileResponse>>()
    val followingList: LiveData<List<UserProfileResponse>> get() = _followingList

    // 팔로워 리스트
    private var _followerList = MutableLiveData<List<UserProfileResponse>>()
    val followerList: LiveData<List<UserProfileResponse>> get() = _followerList

    // 북마크한 큐레이션 리스트
    private var _bookmarkCurationList = MutableLiveData<List<Curation>>()
    val bookmarkCurationList: LiveData<List<Curation>> get() = _bookmarkCurationList

    // 사용자 조회 시 선택된 사용자
    var selectedUser =
        User(
            userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
            userId = ApplicationClass.sharedPreferences.getLong("userId"),
        )

    private var _checkPassword = MutableLiveData<Boolean>()
    val checkPassword: LiveData<Boolean>
        get() = _checkPassword

    fun initCheckPassword() { _checkPassword = MutableLiveData<Boolean>() }

    /**
     * API - 게시글, 팔로우, 팔로잉, petInfo 불러오기
     */
    fun requestMypageInfo(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            try {
                val userEmail = selectedUser.userEmail
                _mypageInfo.value = mypageService.requestMypageInfo(userEmail)
                Log.d(TAG, "userEmail: $userEmail / requestMypageInfo: ${_mypageInfo.value}")
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            } catch (e: Exception) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 비밀번호 확인
     * "userEmail": "string",
     * "userPw": "string"
     */
    fun requestPasswordCheck(password: String, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            try {
                val user = User(
                    ApplicationClass.sharedPreferences.getString("userEmail")!!,
                    password,
                )
                _checkPassword.value = mypageService.requestPasswordCheck(user)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            } catch (e: Exception) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 팔로우 동작
     * userId: 팔로우할 사용자 id
     * user: 나의 userEmail이 담긴 User
     */
    fun followUser(userEmail: String, user: User) {
        Log.d(TAG, "followUser: 팔로우 / 팔로우할 사용자 id: $userEmail, 내 이메일: ${user.userEmail}")
        viewModelScope.launch {
            mypageService.followUser(userEmail, user)
        }
    }

    /**
     * API - 언팔로우 동작
     * userId: 언팔로우할 사용자 id
     * user: 나의 userEmail이 담긴 User
     */
    fun unfollowUser(userEmail: String, user: User) {
        Log.d(TAG, "unfollowUser: 언팔로우 / 언팔로우할 사용자 id: $userEmail, 내 이메일: ${user.userEmail}")
        viewModelScope.launch {
            mypageService.unfollowUser(userEmail, user)
        }
    }

    /**
     * API - 좋아요 누른 게시물 리스트 조회
     * userEmail: 대상 유저
     * currentUser: 내 이메일 정보
     */
    fun requestLikeBoardList(userEmail: String, currentUser: String) {
        Log.d(TAG, "likeBoardList: 좋아요 리스트 조회 / 대상 유저: $userEmail, 내 이메일: $currentUser")
        viewModelScope.launch {
            _likeBoardList.value = mypageService.likeBoardList(userEmail, currentUser)
        }
    }

    /**
     * API - 대상 유저가 팔로우하고 있는 사용자 리스트 조회
     * userEmail: 대상 유저
     * currentUser: 내 이메일 정보
     */
    fun requestFollowingList(userEmail: String, currentUser: String) {
        Log.d(TAG, "requestFollowingList: 팔로잉 리스트 조회 / 대상 유저: $userEmail, 내 이메일: $currentUser")
        viewModelScope.launch {
            _followingList.value = mypageService.followingList(userEmail, currentUser)
        }
    }

    /**
     * 현재 유저가 팔로우 한 목록에 탐색한 유저가 있는지 확인
     * 있다면 -> true 리턴
     */
    fun checkFollowing(): Boolean {
        _followingList.value?.let {
            for (following in it) {
                if (following.userEmail == selectedUser.userEmail) return true
            }
        }
        return false
    }

    /**
     * API - 대상 유저를 팔로우하고 있는 사용자 리스트 조회
     * userEmail: 대상 유저
     * currentUser: 내 이메일 정보
     */
    fun requestFollowerList(userEmail: String, currentUser: String) {
        Log.d(TAG, "requestFollowerList: 팔로워 리스트 조회 / 대상 유저: $userEmail, 내 이메일: $currentUser")
        viewModelScope.launch {
            _followerList.value = mypageService.followerList(userEmail, currentUser)
        }
    }

    /**
     * API - 대상 유저가 북마크한 큐레이션 리스트 조회
     */
    fun userBookmarkedCurations(userEmail: String) {
        Log.d(TAG, "userBookmarkedCurations: 북마크한 리스트 조회 / 유저: $userEmail")
        viewModelScope.launch {
            _bookmarkCurationList.value = mypageService.userBookmarkedCurations(userEmail)
        }
    }

    fun initLikeBoardList() { _likeBoardList = MutableLiveData<List<Board>>() }
    fun initBookmarkCurationList() { _bookmarkCurationList = MutableLiveData<List<Curation>>() }

    /**
     * API - 회원 탈퇴
     * "userEmail": "string",
     * "userPw": "string"
     */
    fun requestSignout(password: String, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            try {
                val user = User(
                    ApplicationClass.sharedPreferences.getString("userEmail")!!,
                    password,
                )
                mypageService.requestSignout(user)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            } catch (e: Exception) {
                mainViewModel.setConnectException()
            }
        }
    }
}
