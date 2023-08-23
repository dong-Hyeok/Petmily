package com.petmily.presentation.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.petmily.config.ApplicationClass
import com.petmily.databinding.CustomLogoutDialogBinding
import com.petmily.databinding.CustomLogoutDialogBinding.inflate
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.MainViewModel

class LogoutDialog(private val context: Context, mainViewModel: MainViewModel) : Dialog(context) {
    private val mainActivity by lazy { context as MainActivity }

    private lateinit var binding: CustomLogoutDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 투명하게 변경
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initBtn()
    }

    private fun initBtn() = with(binding) {
        btnLogoutOk.setOnClickListener {
            ApplicationClass.sharedPreferences.removeUser()
            dismiss()
            (context as MainActivity).changeFragment("login")
            mainActivity.bottomNaviInVisible()
        }

        btnLogoutCancle.setOnClickListener {
            dismiss()
        }
    }
}
