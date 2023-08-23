package com.petmily.presentation.view.curation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentCurationMainBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.CurationViewModel
import com.petmily.repository.dto.Curation

private const val TAG = "petmily_CurationMainFra"
class CurationMainFragment :
    BaseFragment<FragmentCurationMainBinding>(FragmentCurationMainBinding::bind, R.layout.fragment_curation_main) {

    private lateinit var mainActivity: MainActivity

    private val curationViewModel: CurationViewModel by activityViewModels()

    private lateinit var dogAdapter: CurationAdapter
    private lateinit var catAdapter: CurationAdapter
    private lateinit var etcAdapter: CurationAdapter

    private lateinit var snapHelperDog: LinearSnapHelper
    private lateinit var snapHelperCat: LinearSnapHelper
    private lateinit var snapHelperEtc: LinearSnapHelper

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviVisible()
        initBackPressEvent()
        initView()
        initButton()
        initObserver()
        initSnapHelper()
    }

    private fun initBackPressEvent() {
        // 핸드폰 기기 back버튼
        mainActivity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainActivity.bottomNavigationView.selectedItemId = R.id.navigation_page_home
                }
            },
        )
    }

    private fun initView() = with(binding) {
        cdMoveDog.setOnClickListener {
            curationViewModel.fromCuration = "dog"
            mainActivity.changeFragment("curation detail")
        }

        cdMoveCat.setOnClickListener {
            curationViewModel.fromCuration = "cat"
            mainActivity.changeFragment("curation detail")
        }

        cdMoveEtc.setOnClickListener {
            curationViewModel.fromCuration = "etc"
            mainActivity.changeFragment("curation detail")
        }
    }

    private fun initSnapHelper() = with(binding) {
        snapHelperDog = LinearSnapHelper()
        snapHelperDog.attachToRecyclerView(rcvCurationDog)

        snapHelperCat = LinearSnapHelper()
        snapHelperCat.attachToRecyclerView(rcvCurationCat)

        snapHelperEtc = LinearSnapHelper()
        snapHelperEtc.attachToRecyclerView(rcvCurationEtc)
    }

    private fun initButton() = with(binding) {
        ivSearch.setOnClickListener {
            mainActivity.changeFragment("search")
        }

        ivNoti.setOnClickListener {
            mainActivity.changeFragment("notification")
        }
    }

    // Dog
    private fun initDogAdapter() = with(binding) {
        dogAdapter = CurationAdapter(curationViewModel.curationDogList.value, curationViewModel.userBookmarkList).apply {
            itemClickListener = object : CurationAdapter.ItemClickListener {
                override fun itemClick(view: View, curation: Curation, position: Int) {
                    Log.d(TAG, "onClick Dog Curation: $position | ${curation.curl}")
                    curationViewModel.webViewUrl = curation.curl
                    mainActivity.changeFragment("webView")
                }

                override fun bookmarkClick(view: View, isChecked: Boolean, curation: Curation, position: Int) {
                    /**
                     * 북마크 버튼이 토글버튼이라서 누르면 상태가 변화
                     * 서버에서 이미 눌린 북마크는 재요청시 제거
                     * 즉, 해당 큐레이션 북마크만 요청하면 서버에서 눌린상태 자동 갱신 && 화면은 토글버튼으로 UI 자동 변경
                     * 다른 화면 갔다가 왔을때 화면을 다시 그리면 북마크 상태여부 확인 후 체크 하도록 함
                     */
                    curationViewModel.requestCurationBookmark(curation.cid)
                }
            }
        }

        rcvCurationDog.apply {
            adapter = dogAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    // Cat
    private fun initCatAdapter() = with(binding) {
        catAdapter = CurationAdapter(curationViewModel.curationCatList.value, curationViewModel.userBookmarkList).apply {
            itemClickListener = object : CurationAdapter.ItemClickListener {
                override fun itemClick(view: View, curation: Curation, position: Int) {
                    curationViewModel.webViewUrl = curation.curl
                    mainActivity.changeFragment("webView")
                }

                override fun bookmarkClick(view: View, isChecked: Boolean, curation: Curation, position: Int) {
                    if (isChecked) { // 체크 상태
                        curationViewModel.requestCurationBookmark(curation.cid)
                    } else { // 체크 해제 상태
                    }
                }
            }
        }

        rcvCurationCat.apply {
            adapter = catAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    // ETC
    private fun initEtcAdapter() = with(binding) {
        etcAdapter = CurationAdapter(curationViewModel.curationEtcList.value, curationViewModel.userBookmarkList).apply {
            itemClickListener = object : CurationAdapter.ItemClickListener {
                override fun itemClick(view: View, curation: Curation, position: Int) {
                    curationViewModel.webViewUrl = curation.curl
                    mainActivity.changeFragment("webView")
                }

                override fun bookmarkClick(view: View, isChecked: Boolean, curation: Curation, position: Int) {
                    if (isChecked) { // 체크 상태
                        curationViewModel.requestCurationBookmark(curation.cid)
                    } else { // 체크 해제 상태
                    }
                }
            }
        }

        rcvCurationEtc.apply {
            adapter = etcAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initObserver() = with(curationViewModel) {
        // Dog
        curationDogList.observe(viewLifecycleOwner) {
            Log.d(TAG, "requestCurationData - Dog List Size: ${it?.size}")
            initDogAdapter()
            if (!it.isNullOrEmpty()) devideDogCuration(it)
        }

        // Cat
        curationCatList.observe(viewLifecycleOwner) {
            Log.d(TAG, "requestCurationData - Cat List Size: ${it?.size}")
            initCatAdapter()
            if (!it.isNullOrEmpty()) devideCatCuration(it)
        }

        // Etc
        curationEtcList.observe(viewLifecycleOwner) {
            Log.d(TAG, "requestCurationData - Etc List Size: ${it?.size}")
            initEtcAdapter()
            if (!it.isNullOrEmpty()) devideEtcCuration(it)
        }
    }
}
