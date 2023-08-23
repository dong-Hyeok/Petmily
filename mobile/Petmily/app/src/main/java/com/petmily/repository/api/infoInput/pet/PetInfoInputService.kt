package com.petmily.repository.api.infoInput.pet

import android.util.Log
import com.petmily.repository.dto.Pet
import com.petmily.util.RetrofitUtil
import okhttp3.MultipartBody
import java.lang.Exception

private const val TAG = "Petmily_PetInfoInput"
class PetInfoInputService {
    /**
     * 반려동물 정보 등록
     */
    suspend fun petSave(file: MultipartBody.Part?, pet: Pet): Boolean {
        return try {
            RetrofitUtil.petInfoInputApi.petSave(file, pet)
            true
        } catch (e: Exception) {
            Log.d(TAG, "petSave: ${e.message}")
            false
        }
    }

    /**
     * 반려동물 정보 수정
     */
    suspend fun petUpdate(petId: Long, file: MultipartBody.Part?, pet: Pet): Boolean {
        return try {
            RetrofitUtil.petInfoInputApi.petUpdate(petId, file, pet)
            true
        } catch (e: Exception) {
            Log.d(TAG, "petUpdate: ${e.message}")
            false
        }
    }

    /**
     * 반려동물 정보 삭제
     */
    suspend fun petDelete(petId: Long): Boolean {
        return try {
            RetrofitUtil.petInfoInputApi.petDelete(petId)
            true
        } catch (e: Exception) {
            Log.d(TAG, "petDelete: ${e.message}")
            false
        }
    }
}
