package com.petmily.presentation.view.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.petmily.R
import com.petmily.databinding.CustomWithdrawalDialogBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.UserViewModel

class WithDrawalDialog(
    private val context: Context,
    private val userViewModel: UserViewModel,
    private val mainViewModel: MainViewModel,
) : Dialog(context) {

    private lateinit var binding: CustomWithdrawalDialogBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomWithdrawalDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 투명하게 변경
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        init()
        initBtn()
        initObserver()
    }

    private fun init() {
        mainActivity = context as MainActivity
    }

    private fun initObserver() = with(userViewModel) {
        initCheckPassword()
        checkPassword.observe(mainActivity) {
            // 통신 요청 -> 결과
            binding.btnWithdrawalOk.apply {
                isEnabled = it

                if (it) binding.etWithdrawalAuthPass.isEnabled = false

                backgroundTintList = ColorStateList.valueOf(
                    if (it) {
                        resources.getColor(R.color.main_color)
                    } else {
                        Color.GRAY
                    },
                )
            }
        }
    }

    private fun initBtn() = with(binding) {
        // 비밀번호 인증 버튼
        btnWithdrawalAuth.setOnClickListener {
            if (etWithdrawalAuthPass.text.isNullOrBlank()) {
                mainActivity.showSnackbar("비밀번호를 입력하세요.")
            } else {
                userViewModel.requestPasswordCheck(etWithdrawalAuthPass.text.toString(), mainViewModel)
            }
        }

        btnWithdrawalOk.setOnClickListener {
            userViewModel.requestSignout(etWithdrawalAuthPass.text.toString(), mainViewModel)
            dismiss()
            mainActivity.changeFragment("login")
        }

        btnWithdrawalCancle.setOnClickListener {
            dismiss()
        }
    }
}
