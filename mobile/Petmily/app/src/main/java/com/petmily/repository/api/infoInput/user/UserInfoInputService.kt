package com.petmily.repository.api.infoInput.user

import android.annotation.SuppressLint
import android.util.Log
import com.petmily.repository.dto.EditUserInfoResponse
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.repository.dto.UserInfo
import com.petmily.util.RetrofitUtil

private const val TAG = "Petmily_UserInfoInputSe"
class UserInfoInputService {

    /**
     * userInfo의 file은 없으면 null 가능
     */
    suspend fun requestEditMyPage(userInfo: UserInfo): EditUserInfoResponse {
        return try {
            RetrofitUtil.userInfoInputApi.requestEditMyPage(userInfo.file, userInfo.userLoginInfoDto)
        } catch (e: Exception) {
            Log.d(TAG, "requestEditMyPage Exception: ${e.message}")
            EditUserInfoResponse()
        }
    }

    /**
     * user NickName 중복 체크
     */
    suspend fun requestDupNickNameCheck(userLoginInfoDto: UserLoginInfoDto): Boolean {
        return try {
            RetrofitUtil.userInfoInputApi.requestDupNickNameCheck(userLoginInfoDto)
            true
        } catch (e: Exception) {
            Log.d(TAG, "requestEmailCode Exception: ${e.message}")
            false
        }
    }
}
