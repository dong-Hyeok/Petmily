package com.petmily.repository.api.shop

import android.util.Log
import com.petmily.repository.dto.*
import com.petmily.util.RetrofitUtil
import java.lang.Exception

private const val TAG = "Petmily_ShopService"
class ShopService {
    /**
     * API - 아이템 뽑기 요청
     */
    suspend fun requestItem(requestItem: RequestItem): Shop {
        return try {
            Log.d(TAG, "requestItem post: $requestItem")
            val result = RetrofitUtil.shopApi.requestItem(requestItem)
            Log.d(TAG, "requestItem Result: ${result.itemName}")
            return result
        } catch (e: Exception) {
            Shop()
        }
    }

    /**
     * API - 내 인벤토리 데이터 요청
     */
    suspend fun requestMyInventory(userEmail: String): InventoryResult {
        return try {
            val result = RetrofitUtil.shopApi.requestMyInventory(userEmail)
            Log.d(TAG, "requestItem: $result")
            result
        } catch (e: Exception) {
            InventoryResult()
        }
    }

    /**
     * 아이템 장착 요청
     */
    suspend fun requestItemEquipment(equipment: Equipment) {
        try {
            RetrofitUtil.shopApi.requestItemEquipment(equipment)
        } catch (e: Exception) {
            Log.d(TAG, "requestItemEquipment: ${e.message}")
        }
    }

    /**
     * API - 포인트 조회
     */
    suspend fun requestPoint(userEmail: String): Long {
        return try {
            return RetrofitUtil.shopApi.requestPoint(userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "requestPoint: ${e.message}")
            0L
        }
    }

    /**
     * API - 포인트 사용기록 조회
     */
    suspend fun requestPointLog(userEmail: String): MutableList<PointLog> {
        return try {
            return RetrofitUtil.shopApi.requestPointLog(userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "requestPointLog: ${e.message}")
            mutableListOf()
        }
    }

    /**
     * 출석 체크  - 포인트 적립
     */
    suspend fun requestAttendance(userLoginInfoDto: UserLoginInfoDto): Boolean {
        return try {
            Log.d(TAG, "requestAttendance: $userLoginInfoDto")
            RetrofitUtil.shopApi.requestAttendance(userLoginInfoDto)
        } catch (e: Exception) {
            Log.d(TAG, "requestAttendance: ${e.message}")
            false
        }
    }
}
