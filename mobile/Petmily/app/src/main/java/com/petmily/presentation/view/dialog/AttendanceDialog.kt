package com.petmily.presentation.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.petmily.databinding.CustomAttendanceDialogBinding

class AttendanceDialog(context: Context) : Dialog(context) {

    private lateinit var binding: CustomAttendanceDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomAttendanceDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 투명하게 변경
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initButton()
        initLottie()
    }

    private fun initLottie() = with(binding) {
        lottieView.playAnimation()
    }

    private fun initButton() = with(binding) {
        btnOk.setOnClickListener {
            dismiss()
        }
    }
}
