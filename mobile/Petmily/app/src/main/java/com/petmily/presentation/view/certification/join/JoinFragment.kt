package com.petmily.presentation.view.certification.join

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentJoinBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import java.util.regex.Pattern

private const val TAG = "Fetmily_JoinFragment"
class JoinFragment :
    BaseFragment<FragmentJoinBinding>(FragmentJoinBinding::bind, R.layout.fragment_join) {

    lateinit var mainActivity: MainActivity
    private var emailCheck: Boolean = false

    private val userViewModel: UserViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // email 선택 adapter
        val emailAdapter = ArrayAdapter(mainActivity, R.layout.dropdown_email, emailList)
        binding.actEmail.setAdapter(emailAdapter)

        buttonEvent()
        emailClickEvent()
        passwordInputEvent()
        passwordCheckEvent()
        initImageView()

        initObserver()
    }

    // 버튼 이벤트
    private fun buttonEvent() = with(binding) {
        val checkBoxList = listOf(cbAgree1, cbAgree2, cbAgree3, cbAgree4)
        cbAgreeAll.isChecked = checkBoxList.all { it.isChecked }

        // 전체동의 checkBox
        cbAgreeAll.setOnCheckedChangeListener { buttonView, isChecked ->
            checkBoxList.forEach { checkBox ->
                checkBox.isChecked = isChecked
            }
        }

        // 가입하기 버튼
        btnSignup.setOnClickListener {
            if (checkJoin() && mainActivity.isNetworkConnected()) {
                userViewModel.join(
                    idToEmail(etEmail.text.toString(), actEmail.text.toString()),
                    etPassword.text.toString(),
                    mainViewModel,
                )
            }
        }

        // 이메일 인증코드 확인
        btnAuthcode.setOnClickListener {
            if (mainActivity.isNetworkConnected()) {
                userViewModel.checkJoinEmailCode(
                    etAuthcode.text.toString(),
                    idToEmail(etEmail.text.toString(), actEmail.text.toString()),
                    mainViewModel,
                )
            }
        }
    }

    // 최종 가입하기 Check
    private fun checkJoin(): Boolean = with(binding) {
        val inputTextConfirm = etPasswordConfirm.text?.toString()?.trim() ?: ""
        val inputText = etPassword.text?.toString()?.trim() ?: ""
        val joinCheckBoxList = listOf(cbAgree1, cbAgree2, cbAgree3, cbAgree4)
        val agreeCheck = joinCheckBoxList.take(joinCheckBoxList.size - 1).all { it.isChecked }

        if (inputTextConfirm != inputText) {
            mainActivity.showSnackbar("비밀번호를 다시 입력해 주세요.")
        } else if (!emailCheck) {
            mainActivity.showSnackbar("이메일 인증이 필요합니다.")
        } else if (!agreeCheck) {
            mainActivity.showSnackbar("필수 동의가 필요합니다.")
        } else {
            return true
        }

        return false
    }

    // 이메일 인증 요청 처리
    private fun emailClickEvent() = with(binding) {
        // 이메일 확인 버튼
        btnEmailAuth.setOnClickListener {
            // 이메일 정보가 제대로 입력 됐을때 -> 인증 요청 API 실행 -> 결과가 올바르면(인증됬으면 emailCheck를 true로)
            val check = checkEmail()

            if (check) {
                if (mainActivity.isNetworkConnected()) {
                    userViewModel.sendJoinEmailAuth(idToEmail(etEmail.text.toString(), actEmail.text.toString()), mainViewModel)
                }
            } else {
                mainActivity.showSnackbar("잘못된 형식의 이메일 입니다.")
            }
        }
    }

    // 이메일 입력 Check
    private fun checkEmail(): Boolean = with(binding) {
        if (etEmail.text.isNullOrBlank() || actEmail.text.isNullOrBlank()) {
            return false
        } else {
            val email = etEmail.text.toString() + "@" + actEmail.text.toString()
            val pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            return pattern.matcher(email).matches()
        }
    }

    // 비밀번호 입력 처리
    private fun passwordInputEvent() = with(binding) {
        // 비밀번호
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val inputText = etPassword.text?.toString()?.trim() ?: ""

                if (inputText.isNullOrBlank()) {
                    tilPassword.error = "비밀번호를 입력해 주세요."
                } else if (inputText.length < 8) {
                    tilPassword.error = "비밀번호는 8자 이상으로 이루어져야 합니다."
                } else {
                    // 정규 표현식에 매칭되는지 확인
                    val pattern =
                        Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+\$).{8,}\$")
                    val matcher = pattern.matcher(inputText)
                    if (matcher.matches()) {
                        // 유효한 비밀번호 형식일 때
                        tilPassword.isErrorEnabled = false
                    } else {
                        tilPassword.error = "비밀번호는 숫자, 영문이 반드시 포함 되어야 합니다."
                    }
                }
            }
        })
    }

    // 비밀번호 확인 처리
    private fun passwordCheckEvent() = with(binding) {
        etPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val inputTextConfirm = etPasswordConfirm.text?.toString()?.trim() ?: ""
                val inputText = etPassword.text?.toString()?.trim() ?: ""

                if (inputTextConfirm == inputText) {
                    tilPasswordConfirm.isErrorEnabled = false
                } else {
                    tilPasswordConfirm.error = "일치하지 않습니다."
                }
            }
        })
    }

    // LiveData observer 설정
    private fun initObserver() = with(userViewModel) {
        // 이메일 코드 전송
        initJoinEmailCode()
        joinEmailCode.observe(viewLifecycleOwner) {
            if (it.isNullOrBlank()) {
                // 에러, 존재하는 이메일
                Log.d(TAG, "initObserver: 회원가입 코드 전송 실패")
                mainActivity.showSnackbar("이미 존재하는 이메일입니다..")
            } else {
                // 성공
                Log.d(TAG, "initObserver: 회원가입 코드 전송 성공")
                binding.apply {
                    layoutAuthcode.visibility = View.VISIBLE
                }
            }
        }

        // 이메일 코드 인증
        initIsJoinEmailCodeChecked()
        isJoinEmailCodeChecked.observe(viewLifecycleOwner) {
            if (!it) {
                // 에러, 잘못된 인증코드
                Log.d(TAG, "initObserver: 회원가입 코드 인증 실패")
                mainActivity.showSnackbar("잘못된 인증코드입니다.")
            } else {
                // 성공
                Log.d(TAG, "initObserver: 회원가입 코드 인증 성공")
                emailCheck = true

                binding.apply {
                    etAuthcode.apply {
                        isEnabled = false
                        setTextColor(ContextCompat.getColor(mainActivity, android.R.color.darker_gray))
                    }

                    btnAuthcode.text = "성공"
                    btnAuthcode.isEnabled = false
                }
            }
        }

        // 회원가입
        initIsJoined()
        isJoined.observe(viewLifecycleOwner) {
            if (!it) {
                // 에러, 회원가입 실패
                Log.d(TAG, "initObserver: 회원가입 실패")
                mainActivity.showSnackbar("회원가입에 실패하였습니다.")
            } else {
                // 성공
                Log.d(TAG, "initObserver: 회원가입 성공")
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun initImageView() = with(binding) {
        // back
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun idToEmail(id: String, domain: String): String {
        return "$id@$domain"
    }

    companion object {
        val emailList = arrayOf(
            "naver.com",
            "gmail.com",
            "yahoo.com",
            "nate.com",
            "hanmail.com",
        )
    }
}
