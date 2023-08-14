package com.petmily.presentation.view.mypage

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentMyPageBinding
import com.petmily.databinding.ItemBoardBinding
import com.petmily.databinding.ItemSearchCurationBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.dialog.CommentDialog
import com.petmily.presentation.view.dialog.FollowerDialog
import com.petmily.presentation.view.dialog.LogoutDialog
import com.petmily.presentation.view.dialog.OptionDialog
import com.petmily.presentation.view.dialog.WithDrawalDialog
import com.petmily.presentation.view.home.BoardAdapter
import com.petmily.presentation.view.search.SearchCurationAdapter
import com.petmily.presentation.view.search.SearchUserAdapter
import com.petmily.presentation.viewmodel.*
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.ChatParticipant
import com.petmily.repository.dto.Curation
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.util.CheckPermission
import com.petmily.util.GalleryUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyPageFragment :
    BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {

    private val TAG = "petmily_PetInfoFragment"
    private lateinit var mainActivity: MainActivity

    private lateinit var myPetAdapter: MyPetAdapter
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var curationAdapter: SearchCurationAdapter
    private lateinit var followerAdapter: SearchUserAdapter

    private lateinit var galleryUtil: GalleryUtil
    private lateinit var checkPermission: CheckPermission

    private val mainViewModel: MainViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val petViewModel: PetViewModel by activityViewModels()
    private val curationViewModel: CurationViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()

    private val itemList = mutableListOf<Any>() // 아이템 리스트 (NormalItem과 LastItem 객체들을 추가)

    private val commentDialog by lazy { CommentDialog(mainActivity, mainViewModel, boardViewModel) }
    private val optionDialog by lazy { OptionDialog(mainActivity, mainViewModel, boardViewModel) }
    private val followerDialog by lazy { FollowerDialog(mainActivity) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.apply {
            requestMypageInfo(mainViewModel)

            // todo 수정 필요 (두 번째 인자가 꼭 필요한가?)
            requestFollowingList(ApplicationClass.sharedPreferences.getString("userEmail")!!, ApplicationClass.sharedPreferences.getString("userEmail")!!)
        }

        initAdapter()
        initPetItemList()
        initTabLayout()
        initDrawerLayout()
        initImageView()
        initObserver()
        initClickEvent()
        initBackPressEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userViewModel.selectedUserLoginInfoDto =
            UserLoginInfoDto(
                userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                userId = ApplicationClass.sharedPreferences.getLong("userId"),
            )
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

    private fun initUserInfo() = with(binding) {
        Log.d(TAG, "initUserInfo: ${userViewModel.mypageInfo.value}")

        // 본인이면 -> 팔로우, 메시지 버튼 안보이게
        if (ApplicationClass.sharedPreferences.getString("userEmail") == userViewModel.mypageInfo.value!!.userEmail) {
            llFollowWithMessage.visibility = View.INVISIBLE
        } else {
            llFollowWithMessage.visibility = View.VISIBLE
        }

        userViewModel.mypageInfo.value?.apply {
            /**
             * todo 프로필 링 Color (constraintLayout 색 변경해야함)
             */
//            clMypageUserImage.setBackgroundColor(resources.getColor(R.color.favorate_red))

            // 유저 프로필 이미지
            Glide.with(mainActivity)
                .load(this.userProfileImg)
                .circleCrop()
                .into(ivMypageUserImage)

            // 유저 닉네임
            tvUserName.text = this.userNickname

            // 유저 뱃지
//            Glide.with(mainActivity)
//                .load(this?.user)
//                .circleCrop()
//                .into(ivBadge)

            // 게시글, 팔로우, 팔로잉 수
            tvMypageFeedCnt.text = boardCount.toString()
            tvMypageFollowCnt.text = followerCount.toString()
            tvMypageFollowingCnt.text = followingCount.toString()
        }
    }

    private fun initImageView() = with(binding) {
        // 설정창
        ivMypageOption.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // 상점
        ivShopIcon.setOnClickListener {
            mainActivity.changeFragment("shop")
        }
    }

    private fun initDrawerLayout() = with(binding) {
        llDrawerProfile.setOnClickListener { // 프로필 수정
            userViewModel.fromUserInfoInput = "mypage"
            mainActivity.changeFragment("userInfoInput")
        }

        llDrawerPassword.setOnClickListener { // 비밀번호 변경
        }

        llDrawerPoint.setOnClickListener { // 포인트 적립 사용 내역
            // todo API 요청

            mainActivity.changeFragment("pointLog")
        }

        llDrawerSettingNotification.setOnClickListener { // 알림 설정
        }

        llDrawerSettingWithdrawal.setOnClickListener { // 회원 탈퇴
            context?.let { // context가 null이 아닐 때만 다이얼로그를 띄웁니다.
                val dialog = WithDrawalDialog(it, userViewModel, mainViewModel)
                dialog.show()
            }
        }

        llDrawerSettingAppInfo.setOnClickListener { // 앱 정보
        }

        llDrawerLogout.setOnClickListener { // 로그아웃
            context?.let { // context가 null이 아닐 때만 다이얼로그를 띄웁니다.
                val dialog = LogoutDialog(mainActivity, mainViewModel)
                dialog.show()
            }
        }
    }

    private fun initTabLayout() = with(binding) {
        tlMypage.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // 선택된 탭에 따라 RecyclerView의 가시성을 변경합니다.
                when (tab.position) {
                    0 -> {
                        initBoards()
                        rcvMypageBoard.adapter = boardAdapter
                    }

                    1 -> {
                        userViewModel.apply {
                            requestLikeBoardList(
                                selectedUserLoginInfoDto.userEmail,
                                ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                            )
                        }
                        rcvMypageBoard.adapter = boardAdapter
                    }

                    2 -> {
                        userViewModel.apply {
                            userBookmarkedCurations(ApplicationClass.sharedPreferences.getString("userEmail") ?: "")
                        }
                        rcvMypageBoard.adapter = curationAdapter
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
        // NormalItem 등록
        if (!userViewModel.mypageInfo.value?.userPets.isNullOrEmpty()) {
            for (petData in userViewModel.mypageInfo.value!!.userPets) {
                itemList.add(NormalItem(petData))
            }
        }

        // LastItem 등록
        itemList.add(LastItem("Last Item"))
        myPetAdapter.notifyDataSetChanged()
    }

    private fun initAdapter() = with(binding) {
        // pet
        myPetAdapter = MyPetAdapter(itemList, ::onNormalItemClick, ::onLastItemClick)
        rcvMypageMypet.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
        rcvMypageMypet.adapter = myPetAdapter

        // 게시글 adapter
        boardAdapter = BoardAdapter(mainActivity).apply {
            setBoardClickListener(object : BoardAdapter.BoardClickListener {
                override fun heartClick(isClicked: Boolean, binding: ItemBoardBinding, board: Board, position: Int) {
                    if (isClicked) {
                        // 좋아요 등록
                        boardViewModel.registerHeart(
                            board.boardId,
                            ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                        )
                    } else {
                        // 좋아요 취소
                        boardViewModel.deleteHeart(
                            board.boardId,
                            ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                        )
                    }
                }

                override fun commentClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    commentDialog.showCommentDialog(board)
                }

                override fun profileClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    // TODO("Not yet implemented")
                }

                override fun optionClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    boardViewModel.selectedBoard = board
                    optionDialog.showBoardOptionDialog()
                }
            })
        }

        // 큐레이션 adapter
        curationAdapter = SearchCurationAdapter().apply {
            setCurationClickListener(object : SearchCurationAdapter.CurationClickListener {
                override fun curationClick(
                    binding: ItemSearchCurationBinding,
                    curation: Curation,
                    position: Int,
                ) {
                    curationViewModel.webViewUrl = curation.curl
                    mainActivity.changeFragment("webView")
                }
            })
        }

        rcvMypageBoard.apply {
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    // 피드 게시물 데이터 초기화 TODO: api 통신 코드로 변경
    private fun initBoards() {
        boardViewModel.selectAllBoard(userViewModel.selectedUserLoginInfoDto.userEmail, mainViewModel)
    }

    // NormalItem 클릭 이벤트 처리 (등록된 펫 정보 보기) - petViewModel
    private fun onNormalItemClick(normalItem: NormalItem) {
        Log.d(TAG, "onNormalItemClick: $normalItem")
        petViewModel.selectPetInfo = normalItem.pet
        mainActivity.changeFragment("petInfo")
    }

    // LastItem 클릭 이벤트 처리 (신규 펫 등록)
    private fun onLastItemClick(lastItem: LastItem) {
        petViewModel.fromPetInfoInputFragment = "MyPageFragment"
        mainActivity.changeFragment("petInfoInput")
    }

    private fun initObserver() = with(boardViewModel) {
        // 전체 피드 조회
        selectedBoardList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                // 피드 전체 조회 실패
                Log.d(TAG, "selectedBoardList: 피드 전체 조회 실패")
            } else {
                // 피드 전체 조회 성공
                Log.d(TAG, "selectedBoardList: 피드 전체 조회 성공 ${selectedBoardList.value}")
            }
            boardAdapter.setBoards(
                it.filter { board ->
                    (ApplicationClass.sharedPreferences.getString("userEmail") ?: "") == board.userEmail
                }.reversed(),
            )
        }

        // 마이페이지 정보 조회
        userViewModel.mypageInfo.observe(viewLifecycleOwner) {
            initUserInfo()
            initPetItemList()
        }

        // 내 피드 삭제
        initIsBoardDeleted()
        isBoardDeleted.observe(viewLifecycleOwner) {
            if (!it) {
                // 피드 삭제 실패
                mainActivity.showSnackbar("게시물 삭제에 실패하였습니다.")
            } else {
                // 피드 삭제 성공
                mainActivity.showSnackbar("게시물이 삭제되었습니다.")
            }
        }

        // 댓글 등록
        initCommentSaveResult()
        commentSaveResult.observe(viewLifecycleOwner) {
            if (it.commentId == 0L) {
                // 댓글 등록 실패
                mainActivity.showSnackbar("댓글 등록에 실패하였습니다.")
            } else {
                commentDialog.clearEditText()
            }
        }

        // 댓글 삭제
        initIsCommentDeleted()
        isCommentDeleted.observe(viewLifecycleOwner) {
            if (!it) {
                // 댓글 삭제 실패
                mainActivity.showSnackbar("댓글 삭제에 실패하였습니다.")
            } else {
                // 댓글 삭제 성공
            }
            optionDialog.dismiss()
        }

        // 좋아요 누른 게시물 리스트 조회
        userViewModel.apply {
            initLikeBoardList()
            likeBoardList.observe(viewLifecycleOwner) {
                boardAdapter.setBoards(it.map { it.apply { likedByCurrentUser = true } })
            }
        }

        // 북마크한 리스트 조회
        userViewModel.apply {
            initBookmarkCurationList()
            bookmarkCurationList.observe(viewLifecycleOwner) {
                curationAdapter.setCurations(it)
            }
        }

        // 현재 로그인한 유저가 팔로우 한 리스트에서 상대 유저가 있는지 확인
        userViewModel.followingList.observe(viewLifecycleOwner) {
            changeFollowButton(userViewModel.checkFollowing())
        }

        /**
         * 상대방 mypage에서 채팅으로 이동시 -> 룸 ID 리턴 상황 옵저버
         * 상대방의 정보를 저장
         */
        chatViewModel.resultChatRoomId.observe(viewLifecycleOwner) {
            chatViewModel.currentChatOther = ChatParticipant(
                userId = userViewModel.mypageInfo.value?.userId ?: 0L,
                userEmail = userViewModel.mypageInfo.value?.userEmail ?: "",
                userNickname = userViewModel.mypageInfo.value?.userNickname ?: "",
                userProfile = userViewModel.mypageInfo.value?.userProfileImg ?: "",
            )
            mainActivity.changeFragment("chat detail")
        }
    }

    // 팔로우 버튼 상태 변화
    private fun changeFollowButton(status: Boolean) = with(binding.btnFollow) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "changeFollowButton: $status")
            if (status) {
                // text: "언팔로우" 상태
                setTextColor(Color.BLACK)
                isChecked = true
            } else {
                // text: "팔로우" 상태
                setTextColor(Color.WHITE)
                isChecked = false
            }
        }
    }

    private fun initClickEvent() = with(binding) {
        llMypageFollow.setOnClickListener {
            // TODO: Adapter에 데이터 삽입
            followerDialog.showFollowerDialog()
        }
        llMypageFollowing.setOnClickListener {
            // TODO: Adapter에 데이터 삽입
            followerDialog.showFollowerDialog()
        }

        // 프로필 수정 / 팔로우 / 언팔로우 버튼 클릭
        btnFollow.setOnClickListener {
            if (userViewModel.selectedUserLoginInfoDto.userEmail == (ApplicationClass.sharedPreferences.getString("userEmail") ?: "")) {
                // 내 프로필 수정
                userViewModel.fromUserInfoInput = "mypage"
                mainActivity.changeFragment("userInfoInput")
            } else {
                Log.d(TAG, "initClickEvent follow: ${btnFollow.isChecked}")
                if (btnFollow.isChecked) { // 체크된 상태("팔로우" 상태)
                    binding.btnFollow.apply {
                        setTextColor(Color.BLACK)
                        isChecked = true
                    }
                    binding.tvMypageFollowCnt.apply { // 팔로우 or 언팔로우 상태에 따라 보이는 값 변경
                        setText("${text.toString().toInt() + 1}")
                    }
                    userViewModel.followUser(
                        userViewModel.selectedUserLoginInfoDto.userEmail,
                        UserLoginInfoDto(userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: ""),
                    )
                } else {
                    binding.btnFollow.apply {
                        setTextColor(Color.WHITE)
                        isChecked = false
                    }
                    binding.tvMypageFollowCnt.apply {
                        // 팔로우 or 언팔로우 상태에 따라 보이는 값 변경
                        setText("${text.toString().toInt() - 1}")
                    }
                    userViewModel.unfollowUser(
                        userViewModel.selectedUserLoginInfoDto.userEmail,
                        UserLoginInfoDto(userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: ""),
                    )
                }
            }
        }

        /**
         * 상대방 mypage에서 메시지 버튼 클릭 -> 채팅창으로 이동 + 채팅 목록에 추가
         * 1. 채팅방 생성 API 호출 -> 채팅방 ID 수신
         * 2. 채팅방 내용 API 호출 -> 채팅방 내용 수신
         *  => 채팅 보냄(채팅방 ID 받은걸로)
         */
        btnMessage.setOnClickListener {
            chatViewModel.apply {
                // 채팅방 생성 API 요청
                createChatRoom(userViewModel.selectedUserLoginInfoDto.userEmail, mainViewModel)
                // 채팅방 내용 조회 API 요청
                requestChatData(userViewModel.selectedUserLoginInfoDto.userEmail, mainViewModel)
            }
        }
    }
}
