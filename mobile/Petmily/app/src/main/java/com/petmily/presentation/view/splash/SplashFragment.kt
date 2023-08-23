package com.petmily.presentation.view.splash

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentSplashBinding
import com.petmily.presentation.view.MainActivity

class SplashFragment :
    BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::bind, R.layout.fragment_splash) {
    private lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAnimation()
    }

    private fun initAnimation() = with(binding) {
        ivMainLogo.startAnimation(
            AnimationUtils.loadAnimation(mainActivity, R.anim.splash_scale_ani),
        )
    }
}
