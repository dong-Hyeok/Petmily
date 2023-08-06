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
import java.net.ConnectException

private const val TAG = "Petmily_CurationViewModel"

@SuppressLint("LongLogTag")
class CurationViewModel : ViewModel() {
    private val curationService: CurationService by lazy { CurationService() }

    // webView Url
    var webViewUrl = ""

    // mainCuration -> detailCuration
    var fromCuration = ""

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
    private var _randomCurationList = MutableLiveData<MutableList<Curation>>(mutableListOf())
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
     * 모든 동물종 curation 수신
     */
    fun requestCurationData(species: String, mainViewModel: MainViewModel) {
        Log.d(TAG, "requestCurationData: $species")
        viewModelScope.launch {
            try {
                val curationResult = curationService.requestCurationData(species)
                _curationAllList.value = curationResult
                Log.d(TAG, "requestCurationData: ${_curationAllList.value }")
                Log.d(TAG, "requestCurationData - Dog List: ${curationResult.cDogList.size}")
                Log.d(TAG, "requestCurationData - Cat List: ${curationResult.cCatList.size}")

                // 이제 cDogList, cCatList, cEtcList에 접근하여 원하는 작업 수행
                _curationDogList.postValue(curationResult.cDogList)
                _curationCatList.postValue(curationResult.cCatList)
                _curationEtcList.postValue(curationResult.cEtcList)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * Curation bookmark 요청
     */
    fun requestCurationBookmark(curationId: Long, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            try {
                val curationBookmark = CurationBookmark(ApplicationClass.sharedPreferences.getString("userEmail")!!, curationId)
                var result = curationService.requestCurationBookmark(curationBookmark)
                Log.d(TAG, "requestCurationBookmark: $result")
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
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
        var randomCurationList: MutableList<Curation> = mutableListOf()
        for (curation in curations) {
            if (curation != null && curation.isNotEmpty()) {
                val shuffledList = curation.shuffled()
                val curationList = shuffledList.take(2) // 랜덤으로 5개의 아이템 선택 (원하는 개수로 변경 가능)
                Log.d(TAG, "getRandomCurationList: ${_randomCurationList.value}")
                randomCurationList.addAll(curationList)
            }
        }
        _randomCurationList.value = randomCurationList
        Log.d(TAG, "getRandomCurationList all: ${_randomCurationList.value}")
    }
}
