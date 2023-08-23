package com.petmily.repository.api.mypage

import android.util.Log
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.Curation
import com.petmily.repository.dto.MypageInfo
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.repository.dto.UserProfileResponse
import com.petmily.util.RetrofitUtil

private const val TAG = "Petmily_MypageService"
class MypageService {
    /**
     * MainActivity Call
     * UserViewModel Call
     * API - 게시글, 팔로우, 팔로잉, petInfo 불러오기
     */
    suspend fun requestMypageInfo(userEmail: String): MypageInfo {
        return try {
            return RetrofitUtil.mypageApi.requestMypageInfo(userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "requestEmailCode Exception: ${e.message}")
            MypageInfo()
        }
    }

    /**
     * API - 팔로우 동작
     * userId: 팔로우할 사용자 id
     * user: 나의 userEmail이 담긴 User
     */
    suspend fun followUser(userEmail: String, userLoginInfoDto: UserLoginInfoDto) {
        try {
            RetrofitUtil.mypageApi.followUser(userEmail, userLoginInfoDto)
        } catch (e: Exception) {
            Log.d(TAG, "followUser: ${e.message}")
        }
    }

    /**
     * API - 언팔로우 동작
     * userId: 언팔로우할 사용자 id
     * user: 나의 userEmail이 담긴 User
     */
    suspend fun unfollowUser(userEmail: String, userLoginInfoDto: UserLoginInfoDto) {
        try {
            RetrofitUtil.mypageApi.unfollowUser(userEmail, userLoginInfoDto)
        } catch (e: Exception) {
            Log.d(TAG, "unfollowUser: ${e.message}")
        }
    }

    /**
     * API - 좋아요 누른 게시물 리스트 조회
     * userEmail: 대상 유저
     * currentUser: 내 이메일 정보
     */
    suspend fun likeBoardList(userEmail: String, currentUser: String): List<Board> {
        return try {
            RetrofitUtil.mypageApi.likeBoardList(userEmail, currentUser)
        } catch (e: Exception) {
            Log.d(TAG, "likeBoardList: ${e.message}")
            listOf()
        }
    }

    /**
     * API - 비밀번호 확인
     * "userEmail": "string",
     * "userPw": "string"
     */
    suspend fun requestPasswordCheck(userLoginInfoDto: UserLoginInfoDto): Boolean {
        return try {
            RetrofitUtil.mypageApi.requestPasswordCheck(userLoginInfoDto.userEmail, userLoginInfoDto.userPw)
        } catch (e: Exception) {
            Log.d(TAG, "requestEmailCode Exception: ${e.message}")
            false
        }
    }

    /**
     * API - 해당 유저가 작성할 게시글 조회
     */
    suspend fun selectUserBoard(selectedEmail: String, userEmail: String): List<Board> {
        return try {
            RetrofitUtil.mypageApi.selectUserBoard(selectedEmail, userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "boardSelect: ${e.message}")
            mutableListOf()
        }
    }

    /**
     * API - 대상 유저가 팔로우하고 있는 사용자 리스트 조회
     * userEmail: 대상 유저
     * currentUser: 내 이메일 정보
     */
    suspend fun followingList(userEmail: String, currentUser: String): List<UserProfileResponse> {
        return try {
            RetrofitUtil.mypageApi.followingList(userEmail, currentUser)
        } catch (e: Exception) {
            Log.d(TAG, "followingList: ${e.message}")
            listOf()
        }
    }

    /**
     * API - 대상 유저를 팔로우하고 있는 사용자 리스트 조회
     * userEmail: 대상 유저
     * currentUser: 내 이메일 정보
     */
    suspend fun followerList(userEmail: String, currentUser: String): List<UserProfileResponse> {
        return try {
            RetrofitUtil.mypageApi.followerList(userEmail, currentUser)
        } catch (e: Exception) {
            Log.d(TAG, "followerList: ${e.message}")
            listOf()
        }
    }

    /**
     * API - 대상 유저가 북마크한 큐레이션 리스트 조회
     */
    suspend fun userBookmarkedCurations(userEmail: String): List<Curation> {
        return try {
            RetrofitUtil.mypageApi.userBookmarkedCurations(userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "userBookmarkedCurations: ${e.message}")
            listOf()
        }
    }

    /**
     * API - 회원 탈퇴
     * "userEmail": "string",
     * "userPw": "string"
     */
    suspend fun requestSignout(userLoginInfoDto: UserLoginInfoDto): String {
        return try {
            Log.d(TAG, "requestSignout User: $userLoginInfoDto")
            return RetrofitUtil.mypageApi.requestSignout(userLoginInfoDto)
        } catch (e: Exception) {
            Log.d(TAG, "requestSignout Exception: ${e.message}")
            ""
        }
    }
}
