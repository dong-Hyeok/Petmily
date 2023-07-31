package com.petmily.presentation.view.mypage

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.petmily.R
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentMyPageBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.curation.CurationAdapter
import com.petmily.presentation.view.home.BoardAdapter
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.repository.dto.Board
import com.petmily.util.CheckPermission
import com.petmily.util.GalleryUtil

class MyPageFragment :
    BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {

    private lateinit var mainActivity: MainActivity

    private lateinit var myPetAdapter: MyPetAdapter
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var curationAdapter: CurationAdapter

    private lateinit var galleryUtil: GalleryUtil
    private lateinit var checkPermission: CheckPermission
    private val mainViewModel: MainViewModel by activityViewModels()

    private val itemList = mutableListOf<Any>() // 아이템 리스트 (NormalItem과 LastItem 객체들을 추가)

    // 피드 게시물 데이터 TODO: api 통신 후 적용되는 실제 데이터로 변경
    private val boards =
        listOf(
            Board(),
            Board(),
            Board(),
            Board(),
            Board(),
            Board(),
            Board(),
            Board(),
            Board(),
            Board(),
            Board(),
            Board(),
        )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initPetItemList()
        initTabLayout()
        initBoards()
        initDrawerLayout()
        initImageView()
    }

    private fun initImageView() = with(binding) {
        ivMypageOption.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun initDrawerLayout() = with(binding) {
        llDrawerProfile.setOnClickListener { // 프로필 수정
            mainActivity.changeFragment("userInfoInput")
        }

        llDrawerPassword.setOnClickListener { // 비밀번호 변경
        }

        llDrawerPoint.setOnClickListener { // 포인트 적립 사용 내역
        }

        llDrawerSetting.setOnClickListener { // 설정
        }

        llDrawerLogout.setOnClickListener { // 로그아웃
        }
    }

    private fun initTabLayout() = with(binding) {
        llMypageBtn.visibility = View.GONE

        tlMypage.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // 선택된 탭에 따라 RecyclerView의 가시성을 변경합니다.
                when (tab.position) {
                    0 -> {
                        llMypageBtn.visibility = View.GONE
                    }

                    1 -> {
                        llMypageBtn.visibility = View.GONE
                    }

                    2 -> {
                        llMypageBtn.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // 선택 해제된 탭에 따라 RecyclerView의 가시성을 변경합니다.
                // 예를 들어, 선택된 탭이 0번 탭이었고 1번 탭으로 변경되는 경우에는 0번 탭의 RecyclerView를 숨깁니다.
                when (tab.position) {
                    0 -> {
                    }

                    1 -> {
                    }

                    2 -> {
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // 탭이 이미 선택된 상태에서 다시 선택되었을 때의 동작을 정의합니다.
                // 필요한 경우 이 함수를 구현하십시오.
            }
        })
    }

    private fun initPetItemList() {
        itemList.clear()
        itemList.add(NormalItem("Item 1"))
        itemList.add(NormalItem("Item 2"))
        itemList.add(LastItem("Last Item"))
    }

    private fun initAdapter() = with(binding) {
        myPetAdapter = MyPetAdapter(itemList, ::onNormalItemClick, ::onLastItemClick)
        rcvMypageMypet.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
        rcvMypageMypet.adapter = myPetAdapter

        // 게시글 adapter
        boardAdapter = BoardAdapter()
        rcvMypageBoard.apply {
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    // 피드 게시물 데이터 초기화 TODO: api 통신 코드로 변경
    private fun initBoards() {
        boardAdapter.setBoards(boards)
    }

    // NormalItem 클릭 이벤트 처리
    private fun onNormalItemClick(normalItem: NormalItem) {
        // TODO: NormalItem 클릭 이벤트 처리 로직 추가
    }

    // LastItem 클릭 이벤트 처리
    private fun onLastItemClick(lastItem: LastItem) {
        mainActivity.changeFragment("petInfoInput")
    }
}
