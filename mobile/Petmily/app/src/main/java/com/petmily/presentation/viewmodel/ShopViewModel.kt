package com.petmily.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.config.ApplicationClass
import com.petmily.repository.api.shop.ShopService
import com.petmily.repository.dto.*
import kotlinx.coroutines.launch

private const val TAG = "Petmily_ShopViewModel"
class ShopViewModel : ViewModel() {

    private val shopService: ShopService by lazy { ShopService() }

    // 아이템 뽑기 결과 수신
    private var _resultItem = MutableLiveData<Shop>()
    val resultItem: LiveData<Shop>
        get() = _resultItem

    // 포인트 사용 기록 리스트
    private var _resultPointLog = MutableLiveData<MutableList<PointLog>>()
    val resultPointLog: LiveData<MutableList<PointLog>>
        get() = _resultPointLog

    private var _myInventoryItems = MutableLiveData<InventoryResult>()
    val myInventoryItems: LiveData<InventoryResult>
        get() = _myInventoryItems

    private var _resultAll = MutableLiveData<MutableList<Shop>>()
    val resultAll: LiveData<MutableList<Shop>>
        get() = _resultAll

    private var _resultRing = MutableLiveData<MutableList<Shop>>()
    val resultRing: LiveData<MutableList<Shop>>
        get() = _resultRing

    private var _resultBadge = MutableLiveData<MutableList<Shop>>()
    val resultBadge: LiveData<MutableList<Shop>>
        get() = _resultBadge

    private var _resultCover = MutableLiveData<MutableList<Shop>>()
    val resultCover: LiveData<MutableList<Shop>>
        get() = _resultCover

    // point 잔액
    private var _resultPoint = MutableLiveData<Long>()
    val resultPoint: LiveData<Long>
        get() = _resultPoint

    // pointLog

    /**
     * API - 아이템 뽑기 요청
     */
    fun requestItem(item: String) {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: ""
            // 반환 shop 저장
            _resultItem.value = shopService.requestItem(
                RequestItem(
                    userEmail,
                    item,
                ),
            )
            Log.d(TAG, "requestItem: ${_resultItem.value}")
        }
    }

    /**
     * API - 내 인벤토리 데이터 요청
     */
    fun requestMyInventory() {
        viewModelScope.launch {
            try {
                val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")!!
                // 반환 shop 저장
                _myInventoryItems.value = shopService.requestMyInventory(userEmail)

                _resultAll.value = mutableListOf()
                _myInventoryItems.value?.lingList?.let { _resultAll.value?.addAll(it) }
                _myInventoryItems.value?.badgeList?.let { _resultAll.value?.addAll(it) }
                _myInventoryItems.value?.backgroundList?.let { _resultAll.value?.addAll(it) }

                _resultRing.value = _myInventoryItems.value?.lingList
                _resultBadge.value = _myInventoryItems.value?.badgeList
                _resultCover.value = _myInventoryItems.value?.backgroundList

                Log.d(TAG, "requestMyInventory: ${_resultAll.value}")
            } catch (e: Exception) {
                _myInventoryItems.value = InventoryResult()
            }
        }
    }

    /**
     * API - 아이템 장착 요청
     */
    fun requestItemEquipment(item: Shop) {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: ""
            // 반환 shop 저장
            shopService.requestItemEquipment(
                Equipment(
                    userEmail,
                    item.itemId,
                    item.itemType,
                ),
            )
        }
    }

    /**
     * API - 포인트 조회
     */
    fun requestPoint() {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")
            _resultPoint.value = shopService.requestPoint(userEmail!!)
            Log.d(TAG, "requestPoint: ${_resultPoint.value}")
        }
    }

    /**
     * API - 포인트 사용기록 조회
     */
    fun requestPointLog() {
        viewModelScope.launch {
            val userEmail = ApplicationClass.sharedPreferences.getString("userEmail")
            _resultPointLog.value = shopService.requestPointLog(userEmail!!)
        }
    }

    fun initResultItem() { _resultItem = MutableLiveData<Shop>()
    }
}
