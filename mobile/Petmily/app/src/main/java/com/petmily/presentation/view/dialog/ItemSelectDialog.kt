package com.petmily.presentation.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.petmily.databinding.CustomItemSelectDialogBinding
import com.petmily.presentation.viewmodel.ShopViewModel
import com.petmily.repository.dto.Shop

class ItemSelectDialog(
    context: Context,
    private val item: Shop,
    private val shopViewModel: ShopViewModel,
) : Dialog(context) {

    private lateinit var binding: CustomItemSelectDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomItemSelectDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 투명하게 변경
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initButtom()
    }

    private fun initButtom() = with(binding) {
        btnOk.setOnClickListener {
            shopViewModel.requestItemEquipment(item)
            dismiss()
        }

        btnCancle.setOnClickListener {
            dismiss()
        }
    }
}
