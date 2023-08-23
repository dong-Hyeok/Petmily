package com.petmily.repository.api.shop

import com.petmily.repository.dto.*
import retrofit2.http.*

interface ShopApi {

    /**
     * API - 아이템 뽑기 요청
     */
    @POST("/item/getRandom")
    suspend fun requestItem(
        @Body body: RequestItem,
    ): Shop

    /**
     * API - 내 인벤토리 데이터 요청
     */
    @GET("/item/inventory")
    suspend fun requestMyInventory(
        @Query("userEmail") userEmail: String,
    ): InventoryResult

    /**
     * 아이템 장착 요청
     */
    @PUT("/item/equipment")
    suspend fun requestItemEquipment(@Body body: Equipment)

    /**
     * API - 포인트 조회
     */
    @GET("/userpoint")
    suspend fun requestPoint(@Query("userEmail") userEmail: String): Long

    /**
     * API - 포인트 사용기록 조회
     */
    @GET("/usagePoint")
    suspend fun requestPointLog(@Query("userEmail") userEmail: String): MutableList<PointLog>

    /**
     * 출석 체크  - 포인트 적립
     */
    @PUT("/attendance")
    suspend fun requestAttendance(@Body body: UserLoginInfoDto): Boolean
}
