package com.petmily.presentation.view.certification.password

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.system.Os.bind
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentPasswordBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.UserViewModel

private const val TAG = "Fetmily_PasswordFragmen"
class PasswordFragment :
    BaseFragment<FragmentPasswordBinding>(
        FragmentPasswordBinding::bind,
        R.layout.fragment_password,
    ) {

    private lateinit var mainActivity: MainActivity
    private val userViewModel: UserViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initImageView()
        initButton()
        initObserve()
    }

    private fun initButton() = with(binding) {
        // 이메일 확인(인증코드 요청 받음)
        btnEmailConfirm.setOnClickListener {
            userViewModel.sendPassEmailAuth(etAuthEmail.text.toString(), mainViewModel)
        }

        // 인증코드 확인 요청(이메일, 인증코드)
        btnCodeConfirm.setOnClickListener {
            userViewModel.checkPasswordEmailCode(
                etAuthCode.text.toString(),
                etAuthEmail.text.toString(),
                mainViewModel,
            )
        }

        // 완료 버튼(사용자에게 변경된 비밀번호 전송)
        btnChangePassword.setOnClickListener {
            userViewModel.changePassword(userViewModel.checkSuccessEmail, mainViewModel)
        }
    }

    private fun initImageView() = with(binding) {
        // back
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initObserve() = with(userViewModel) {
        initPwdEmailCode()
        pwdEmailCode.observe(viewLifecycleOwner) {
            if (it.isNullOrBlank()) {
                // 에러, 존재하는 이메일
                Log.d(TAG, "initObserver: 비밀번호 재설정 인증 실패")
                mainActivity.showSnackbar("존재하지 않는 이메일입니다.")
            } else {
                // 성공
                Log.d(TAG, "initObserver: 비밀번호 재설정 인증 코드 전송 성공 ${pwdEmailCode.value}")
                binding.layoutAuthcode.visibility = View.VISIBLE
            }
        }

        initIsPwdEmailCodeChecked()
        isPwdEmailCodeChecked.observe(viewLifecycleOwner) {
            if (!it) {
                // 에러, 잘못된 인증코드
                Log.d(TAG, "initObserver: 비밀번호 재설정 인증 코드 인증 실패")
                mainActivity.showSnackbar("잘못된 인증코드입니다.")
            } else {
                // 성공
                Log.d(TAG, "initObserver: 비밀번호 재설정 코드 인증 성공")

                mainActivity.showSnackbar("이메일 인증 완료")
                binding.apply {
                    etAuthEmail.apply {
                        isEnabled = false
                        setTextColor(
                            ContextCompat.getColor(
                                mainActivity,
                                android.R.color.darker_gray,
                            ),
                        )
                    }
                    etAuthCode.apply {
                        isEnabled = false
                        setTextColor(
                            ContextCompat.getColor(
                                mainActivity,
                                android.R.color.darker_gray,
                            ),
                        )
                    }

                    btnEmailConfirm.visibility = View.INVISIBLE
                    btnCodeConfirm.visibility = View.INVISIBLE
                }
            }
        }

        initIsChangePassword()
        isChangepPassword.observe(viewLifecycleOwner) {
            if (!it) {
                // 에러, 비밀번호 재설정 실패
                Log.d(TAG, "initObserver: 비밀번호 재설정")
                mainActivity.showSnackbar("비밀번호 재설정에 실패하였습니다.")
            } else {
                // 비밀번호 재설정 성공
                Log.d(TAG, "initObserver: 비밀번호 재설정 성공")
                parentFragmentManager.popBackStack()
            }
        }
    }
}
