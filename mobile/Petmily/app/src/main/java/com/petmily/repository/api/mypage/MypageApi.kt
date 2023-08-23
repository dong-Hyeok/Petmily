package com.petmily.repository.api.mypage

import com.petmily.repository.dto.Board
import com.petmily.repository.dto.Curation
import com.petmily.repository.dto.MypageInfo
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.repository.dto.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MypageApi {
    // 유저 정보 GET
    @GET("/profile/{userEmail}")
    suspend fun requestMypageInfo(@Path("userEmail") userEmail: String): MypageInfo

    @POST("/follow/{userEmail}")
    suspend fun followUser(
        @Path("userEmail") userEmail: String,
        @Body userLoginInfoDto: UserLoginInfoDto,
    )

    @HTTP(method = "DELETE", path = "/follow/{userEmail}", hasBody = true)
    suspend fun unfollowUser(
        @Path("userEmail") userEmail: String,
        @Body userLoginInfoDto: UserLoginInfoDto,
    )

    @GET("/profile/{userEmail}/likeboard")
    suspend fun likeBoardList(
        @Path("userEmail") userEmail: String,
        @Query("currentUser") currentUser: String,
    ): List<Board>

    /**
     * API - 해당 유저가 작성할 게시글 조회
     */
    @GET("/profile/{userEmail}/writeboard")
    suspend fun selectUserBoard(
        @Path("userEmail") userEmail: String,
        @Query("currentUser") currentUser: String,
    ): List<Board>

    @GET("/profile/{userEmail}/following")
    suspend fun followingList(
        @Path("userEmail") userEmail: String,
        @Query("currentUser") currentUser: String,
    ): List<UserProfileResponse>

    @GET("/profile/{userEmail}/follower")
    suspend fun followerList(
        @Path("userEmail") userEmail: String,
        @Query("currentUser") currentUser: String,
    ): List<UserProfileResponse>

    @GET("/curation/userbookmarksdetail")
    suspend fun userBookmarkedCurations(
        @Query("userEmail") userEmail: String,
    ): List<Curation>

    // 회원 탈퇴 비밀번호 check
    @GET("/signout/passwordcheck")
    suspend fun requestPasswordCheck(
        @Query("userEmail") userEmail: String,
        @Query("userPw") userPw: String,
    ): Boolean

    // 회원 탈퇴 요청
    @HTTP(method = "DELETE", path = "/signout/deleteuser", hasBody = true)
    suspend fun requestSignout(@Body body: UserLoginInfoDto): String
}
