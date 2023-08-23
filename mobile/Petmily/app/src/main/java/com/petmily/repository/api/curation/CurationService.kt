package com.petmily.repository.api.curation

import android.util.Log
import com.petmily.repository.dto.CurationBookmark
import com.petmily.repository.dto.CurationResult
import com.petmily.util.RetrofitUtil

private const val TAG = "Petmily_CurationService"

class CurationService {

    /**
     * Curation data 요청
     */
    suspend fun requestCurationData(species: String): CurationResult {
        return try {
            return RetrofitUtil.curationApi.requestCurationData(species)
        } catch (e: Exception) {
            CurationResult()
        }
    }

    /**
     * Curation bookmark 요청
     */
    suspend fun requestCurationBookmark(curationBookmark: CurationBookmark): MutableList<Long> {
        return try {
            return RetrofitUtil.curationApi.requestCurationBookmark(curationBookmark)
        } catch (e: Exception) {
            Log.d(TAG, "request curation: ${e.message}")
            mutableListOf()
        }
    }
}
