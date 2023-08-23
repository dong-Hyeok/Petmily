package com.petmily.repository.api.board

import com.petmily.repository.dto.Board
import com.petmily.repository.dto.HashTagRequestDto
import com.petmily.repository.dto.UserLoginInfoDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BoardApi {
    @Multipart
    @POST("/board/save")
    suspend fun boardSave(
        @Part file: List<MultipartBody.Part>?,
        @Part("boardRequestDto") boardRequestDto: Board,
        @Part("hashTagRequestDto") hashTagRequestDto: HashTagRequestDto,
    )

    @Multipart
    @POST("/board/{boardId}")
    suspend fun boardUpdate(
        @Path("boardId") boardId: Long,
        @Part file: List<MultipartBody.Part>?,
        @Part("boardRequestDto") boardRequestDto: Board,
        @Part("hashTagRequestDto") hashTagRequestDto: HashTagRequestDto,
    )

    @HTTP(method = "DELETE", path = "/board/{boardId}", hasBody = true)
    suspend fun boardDelete(
        @Path("boardId") boardId: Long,
        @Body userLoginInfoDtoEmail: UserLoginInfoDto,
    )

    @GET("/board/all")
    suspend fun boardSelectAll(
        @Query("currentUserEmail") currentUserEmail: String,
    ): List<Board>

    @GET("/board/{boardId}")
    suspend fun boardSelectOne(
        @Path("boardId") boardId: Long,
        @Query("currentUserEmail") currentUserEmail: String,
    ): Board

    @POST("/board/heart")
    suspend fun registerHeart(
        @Body body: Board,
    )

    @HTTP(method = "DELETE", path = "/board/heart", hasBody = true)
    suspend fun deleteHeart(
        @Body body: Board,
    )

    @GET("/board/search/{hashTag}")
    suspend fun searchBoard(
        @Path("hashTag") hashTag: String,
        @Query("currentUser") currentUser: String,
    ): List<Board>
}
