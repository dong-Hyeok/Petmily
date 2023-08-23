package com.petmily.repository.api.walk

import android.util.Log
import com.petmily.repository.dto.WalkInfoResponse
import com.petmily.util.RetrofitUtil

private const val TAG = "Fetmily_WalkService"
class WalkService {
    /**
     * 산책정보 저장
     */
    suspend fun saveWalk(petId: Long, walkDate: String, walkDistance: Int, walkSpend: Int): Boolean {
        return try {
            RetrofitUtil.walkApi.saveWalk(petId, walkDate, walkDistance, walkSpend)
            true
        } catch (e: Exception) {
            Log.d(TAG, "saveWalk: ${e.message}")
            false
        }
    }

    /**
     * 사용자의 반려동물 산책 정보 전체 조회
     */
    suspend fun userPetWalkInfo(userEmail: String): List<WalkInfoResponse> {
        return try {
            RetrofitUtil.walkApi.userPetWalkInfo(userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "userPetWalkInfo: ${e.message}")
            listOf()
        }
    }
}
