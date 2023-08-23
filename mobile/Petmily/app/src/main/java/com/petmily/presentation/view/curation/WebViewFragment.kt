package com.petmily.presentation.view.curation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentWebViewBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.CurationViewModel

class WebViewFragment :
    BaseFragment<FragmentWebViewBinding>(FragmentWebViewBinding::bind, R.layout.fragment_web_view) {

    private lateinit var mainActivity: MainActivity

    private val curationViewModel: CurationViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviInVisible()
        showWebView(curationViewModel.webViewUrl)

        initButton()
    }

    private fun initButton() = with(binding) {
        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showWebView(url: String) = with(binding) {
        // WebView 설정
        wvCuration.apply {
            webViewClient = WebViewClient() // 하이퍼링크 클릭시 새창 띄우기 방지
            webChromeClient = WebChromeClient() // 크롬환경에 맞는 세팅을 해줌. 특히, 알람등을 받기위해서는 꼭 선언해주어야함 (alert같은 경우)
        }
        wvCuration.settings.apply {
            javaScriptEnabled = true // 자바스크립트 허용
            domStorageEnabled = true // 로컬 스토리지 사용 여부를 설정하는 속성으로 팝업창등을 '하루동안 보지 않기' 기능 사용에 필요
            defaultTextEncodingName = "UTF-8" // 인코딩 설정
            supportMultipleWindows()
        }

        // URL 열기
        wvCuration.loadUrl(url)
    }
}
