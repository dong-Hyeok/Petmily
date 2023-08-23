package com.petmily.presentation.view.store

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentInventoryBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.dialog.ItemSelectDialog
import com.petmily.presentation.viewmodel.ShopViewModel
import com.petmily.repository.dto.Shop

class InventoryFragment :
    BaseFragment<FragmentInventoryBinding>(FragmentInventoryBinding::bind, R.layout.fragment_inventory) {

    private lateinit var mainActivity: MainActivity
    private lateinit var shopAdapter: InventoryAdapter

    private val shopViewModel: ShopViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAPI()
        initAdapter()
        initObserve()
        initButton()
    }

    private fun initAPI() {
        // API 요청 - my 인벤토리 아이템
        shopViewModel.requestMyInventory()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initButton() = with(binding) {
        btnAll.apply {
            setTextColor(Color.WHITE)
            isChecked = true
        }

        btnAll.setOnClickListener {
            shopViewModel.resultAll.value?.let { it -> shopAdapter.setMyItemList(it) }
            shopAdapter.notifyDataSetChanged()
            changeBtnStatus(btnAll)
        }

        btnRing.setOnClickListener {
            shopViewModel.resultRing.value?.let { it -> shopAdapter.setMyItemList(it) }
            shopAdapter.notifyDataSetChanged()
            changeBtnStatus(btnRing)
        }

        btnBadge.setOnClickListener {
            shopViewModel.resultBadge.value?.let { it -> shopAdapter.setMyItemList(it) }
            shopAdapter.notifyDataSetChanged()
            changeBtnStatus(btnBadge)
        }

        btnCover.setOnClickListener {
            shopViewModel.resultCover.value?.let { it -> shopAdapter.setMyItemList(it) }
            shopAdapter.notifyDataSetChanged()
            changeBtnStatus(btnCover)
        }
    }

    // 상단 4개 버튼 상태 변화 로직
    private fun changeBtnStatus(toggleButton: ToggleButton) = with(binding) {
        val btnGroup = listOf(btnAll, btnRing, btnBadge, btnCover)
        for (btn in btnGroup) {
            btn.apply {
                setTextColor(Color.BLACK)
                isChecked = false
            }
        }
        toggleButton.apply {
            setTextColor(Color.WHITE)
            isChecked = true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserve() = with(shopViewModel) {
        resultAll.observe(viewLifecycleOwner) {
            shopAdapter.setMyItemList(it)
            shopAdapter.notifyDataSetChanged()
        }
    }

    private fun initAdapter() = with(binding) {
        shopAdapter = InventoryAdapter().apply {
            itemClickListener = object : InventoryAdapter.ItemClickListener {
                override fun itemClick(view: View, item: Shop, position: Int) {
                    val dialog = ItemSelectDialog(mainActivity, item, shopViewModel)
                    dialog.show()
                }
            }
        }

        rcvShop.apply {
            adapter = shopAdapter
            layoutManager = GridLayoutManager(mainActivity, 3, GridLayoutManager.VERTICAL, false)
        }
    }
}
