package com.petmily.presentation.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.config.ApplicationClass
import com.petmily.repository.api.curation.CurationService
import com.petmily.repository.dto.Curation
import com.petmily.repository.dto.CurationBookmark
import com.petmily.repository.dto.CurationResult
import kotlinx.coroutines.launch

private const val TAG = "Petmily_CurationViewMod"
class CurationViewModel : ViewModel() {
    private val curationService: CurationService by lazy { CurationService() }

    // webView Url
    var webViewUrl = ""

    // mainCuration -> detailCuration
    var fromCuration = ""

    // user Bookmark List
    var userBookmarkList = HashSet<Long>()

    // All
    private var _curationAllList = MutableLiveData<CurationResult>()
    val curationAllList: LiveData<CurationResult>
        get() = _curationAllList

    // 강아지
    private var _curationDogList = MutableLiveData<MutableList<Curation>>()
    val curationDogList: LiveData<MutableList<Curation>>
        get() = _curationDogList

    // 고양이
    private var _curationCatList = MutableLiveData<MutableList<Curation>>()
    val curationCatList: LiveData<MutableList<Curation>>
        get() = _curationCatList

    // 기타 동물
    private var _curationEtcList = MutableLiveData<MutableList<Curation>>()
    val curationEtcList: LiveData<MutableList<Curation>>
        get() = _curationEtcList

    // home 큐레이션 (랜덤)
    private var _randomCurationList = MutableLiveData<MutableList<Curation>>()
    val randomCurationList: LiveData<MutableList<Curation>>
        get() = _randomCurationList

    // 건강
    var dogHealthList: MutableList<Curation> = mutableListOf()
    var catHealthList: MutableList<Curation> = mutableListOf()
    var etcHealthList: MutableList<Curation> = mutableListOf()

    // 미용
    var dogBeautyList: MutableList<Curation> = mutableListOf()
    var catBeautyList: MutableList<Curation> = mutableListOf()
    var etcBeautyList: MutableList<Curation> = mutableListOf()

    // 식품
    var dogFeedList: MutableList<Curation> = mutableListOf()
    var catFeedList: MutableList<Curation> = mutableListOf()
    var etcFeedList: MutableList<Curation> = mutableListOf()

    // 입양
    var dogAdoptList: MutableList<Curation> = mutableListOf()
    var catAdoptList: MutableList<Curation> = mutableListOf()
    var etcAdoptList: MutableList<Curation> = mutableListOf()

    /**
     * API - 모든 동물종 curation 수신
     */
    fun requestCurationData(species: String) {
        Log.d(TAG, "requestCurationData 데이터 요청: $species")
        viewModelScope.launch {
            try {
                val curationResult = curationService.requestCurationData(species)
                Log.d(TAG, "requestCurationData - Dog List: ${curationResult.cDogList.size}")
                Log.d(TAG, "requestCurationData - Cat List: ${curationResult.cCatList.size}")
                Log.d(TAG, "requestCurationData - Etc List: ${curationResult.cEtcList.size}")

                // 이제 cDogList, cCatList, cEtcList에 접근하여 원하는 작업 수행
                _curationDogList.value = curationResult.cDogList
                _curationCatList.value = curationResult.cCatList
                _curationEtcList.value = curationResult.cEtcList

                _curationAllList.value = curationResult

                Log.d(TAG, "requestCurationData: ${_curationAllList.value }")
            } catch (e: Exception) {
                // 큐레이션 조회에 실패해도 home으로 이동해야하므로 강제로 값 설정
                _curationAllList.value = CurationResult()
            }
        }
    }

    /**
     * Curation bookmark 요청
     */
    fun requestCurationBookmark(curationId: Long) {
        viewModelScope.launch {
            val curationBookmark = CurationBookmark(ApplicationClass.sharedPreferences.getString("userEmail")!!, curationId)
            // 북마크 등록 -> 반환값(북마크 리스트) -> HashSet으로 변환
            userBookmarkList = curationService.requestCurationBookmark(curationBookmark).toHashSet()
            Log.d(TAG, "requestCurationBookmark: $userBookmarkList")
        }
    }

    // dog 건강, 미용, 식품, 입양 분류
    fun devideDogCuration(curationList: MutableList<Curation>) {
        for (curation in curationList) {
            when (curation.ccategory) {
                "건강" -> dogHealthList.add(curation)
                "미용" -> dogBeautyList.add(curation)
                "식품" -> dogFeedList.add(curation)
                "입양" -> dogAdoptList.add(curation)
            }
        }
    }

    // cat 건강, 미용, 식품, 입양 분류
    fun devideCatCuration(curationList: MutableList<Curation>) {
        for (curation in curationList) {
            when (curation.ccategory) {
                "건강" -> catHealthList.add(curation)
                "미용" -> catBeautyList.add(curation)
                "식품" -> catFeedList.add(curation)
                "입양" -> catAdoptList.add(curation)
            }
        }
    }

    // etc 건강, 미용, 식품, 입양 분류
    fun devideEtcCuration(curationList: MutableList<Curation>) {
        for (curation in curationList) {
            when (curation.ccategory) {
                "건강" -> etcHealthList.add(curation)
                "미용" -> etcBeautyList.add(curation)
                "식품" -> etcFeedList.add(curation)
                "입양" -> etcAdoptList.add(curation)
            }
        }
    }

    /**
     *  home 큐레이션 (랜덤)
     */
    fun getRandomCurationList() {
        val curations = listOf(_curationDogList.value, _curationCatList.value, _curationEtcList.value)
        val randomCurationList: MutableList<Curation> = mutableListOf()
        for (curation in curations) {
            if (!curation.isNullOrEmpty()) {
                val shuffledList = curation.shuffled()
                val curationList = shuffledList.take(2) // 랜덤으로 5개의 아이템 선택 (원하는 개수로 변경 가능)
                randomCurationList.addAll(curationList)
            }
        }
        _randomCurationList.value = randomCurationList
        Log.d(TAG, "getRandomCurationList all: ${_randomCurationList.value}")
    }
}
