package com.petmily.presentation.view.store

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentPurchaseBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.dialog.DrawingDialog
import com.petmily.presentation.viewmodel.ShopViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "fetmily_PurchaseFragme"
class PurchaseFragment :
    BaseFragment<FragmentPurchaseBinding>(FragmentPurchaseBinding::bind, R.layout.fragment_purchase) {

    private lateinit var mainActivity: MainActivity

    private val shopViewModel: ShopViewModel by activityViewModels()

    private val dialog: DrawingDialog by lazy { DrawingDialog(requireContext()) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserve()
        initView()
        initLottie()
    }

    private fun initView() = with(binding) {
        lottieRingCoin.setOnClickListener {
            lottieRingCoin.apply {
                playAnimation()
                speed = 1.5F
                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        if (shopViewModel.resultPoint.value!! >= 20) {
                            showDialog()
                            requestItem("ring")
                        } else {
                            mainActivity.showSnackbar("포인트가 부족합니다.")
                        }
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }
        }

        lottieBadgeCoin.setOnClickListener {
            lottieBadgeCoin.apply {
                playAnimation()
                speed = 1.5F
                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        if (shopViewModel.resultPoint.value!! >= 20) {
                            showDialog()
                            requestItem("badge")
                        } else {
                            mainActivity.showSnackbar("포인트가 부족합니다.")
                        }
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }
        }

        lottieCoverCoin.setOnClickListener {
            lottieCoverCoin.apply {
                playAnimation()
                speed = 1.5F
                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        if (shopViewModel.resultPoint.value!! >= 30) {
                            showDialog()
                            requestItem("background")
                        } else {
                            mainActivity.showSnackbar("포인트가 부족합니다.")
                        }
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }
        }

        lottieAllCoin.setOnClickListener {
            lottieAllCoin.apply {
                playAnimation()
                speed = 1.5F
                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        if (shopViewModel.resultPoint.value!! >= 10) {
                            showDialog()
                            requestItem("All")
                        } else {
                            mainActivity.showSnackbar("포인트가 부족합니다.")
                        }
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }
        }
    }

    /**
     *   아이템 뽑기 요청
     */
    private fun requestItem(item: String) {
        Log.d(TAG, "requestItem: ")
        shopViewModel.requestItem(item)
    }

    /**
     * 다이얼로그 띄우고 동시에 API요청으로 상품 결과 수신
     */
    private fun showDialog() {
        // 다이얼로그 show
        Log.d(TAG, "showDialog: ")
        context?.let { // context가 null이 아닐 때만 다이얼로그를 띄웁니다.
            dialog.show()
        }
    }

    /**
     * 결과 수신하면 -> 결과 다이얼로그에 결과 표시
     */
    private fun initObserve() = with(shopViewModel) {
        // 아이템 뽑기 결과 (꽝, 성공 분기)
        initResultItem()
        resultItem.observe(viewLifecycleOwner) {
            dialog.stopFirstLottie(it)
            requestPoint() // 잔액 update
        }
    }

    private fun initLottie() = with(binding) {
        lottieLeft.apply {
            // 애니메이션 재생
            playAnimation()

            addAnimatorListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    // 애니메이션이 종료된 후 4초 후에 다시 애니메이션을 재생
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        playAnimation()
                    }
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }

        lottieMiddle.apply {
            playAnimation()

            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    // 애니메이션이 종료된 후 4초 후에 다시 애니메이션을 재생
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        playAnimation()
                    }
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }

        lottieRight.apply {
            playAnimation()

            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    // 애니메이션이 종료된 후 4초 후에 다시 애니메이션을 재생
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        playAnimation()
                    }
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }
}
