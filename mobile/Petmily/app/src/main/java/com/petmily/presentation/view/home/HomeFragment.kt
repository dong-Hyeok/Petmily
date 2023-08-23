package com.petmily.presentation.view.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentHomeBinding
import com.petmily.databinding.ItemBoardBinding
import com.petmily.databinding.ItemHomeCurationBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.dialog.AttendanceDialog
import com.petmily.presentation.view.dialog.CommentDialog
import com.petmily.presentation.view.dialog.OptionDialog
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.CurationViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.Curation
import com.petmily.repository.dto.UserLoginInfoDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

private const val TAG = "petmily_HomeFragment"
private const val CURATION_JOB_DELAY = 3000L
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {

    private lateinit var mainActivity: MainActivity

    private lateinit var homeCurationAdapter: HomeCurationAdapter
    private lateinit var boardAdapter: BoardAdapter

    private val mainViewModel: MainViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()
    private val curationViewModel: CurationViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    // curation ViewPager 자동 스크롤 job
    private lateinit var curationJob: Job

    // 댓글 BottomSheetDialog
    private val commentDialog by lazy { CommentDialog(mainActivity, mainViewModel, boardViewModel) }

    // 3점(옵션) BottomSheetDialog
    private val optionDialog by lazy { OptionDialog(mainActivity, mainViewModel, boardViewModel) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        checkAttendance()
        initAdapter()
        initBoards()
        initBtn()
        mainActivity.bottomNaviVisible()
    }

    /**
     * 출석 체크
     */
    fun checkAttendance() {
        // API 통신 - 포인트 ++
        mainViewModel.requestAttendance()
    }

    override fun onResume() {
        super.onResume()
        if (!curationViewModel.randomCurationList.value.isNullOrEmpty() &&
            curationViewModel.randomCurationList.value!!.isNotEmpty()
        ) {
            curationJobCreate()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::curationJob.isInitialized) curationJob.cancel()
    }

    private fun initAdapter() = with(binding) {
        // 클릭 이벤트 처리
        homeCurationAdapter = HomeCurationAdapter(curationViewModel).apply {
            setCurationClickListener(object : HomeCurationAdapter.CurationClickListener {
                override fun curationClick(
                    binding: ItemHomeCurationBinding,
                    curation: Curation,
                    position: Int,
                ) {
                    curationViewModel.webViewUrl = curation.curl
                    mainActivity.changeFragment("webView")
                }
            })
        }
        boardAdapter = BoardAdapter(mainActivity).apply {
            setBoardClickListener(object : BoardAdapter.BoardClickListener {
                // 좋아요 클릭
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

                // 댓글 버튼 클릭
                override fun commentClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    // commentDialog 선언 및 show
                    commentDialog.showCommentDialog(board)
                }

                // 프로필 이미지 및 이름 클릭
                override fun profileClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    userViewModel.selectedUserLoginInfoDto = UserLoginInfoDto(userEmail = board.userEmail)
                    mainActivity.changeFragment("my page")
                }

                // 옵션(3점) 클릭
                override fun optionClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    boardViewModel.selectedBoard = board
                    optionDialog.showBoardOptionDialog()
                }
            })
        }

        vpCuration.adapter = homeCurationAdapter
        rcvBoard.apply {
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }
    }

    // 피드 게시물 데이터 초기화
    private fun initBoards() {
        // 로딩 시작
        loadingStart()
        boardViewModel.selectAllBoard(ApplicationClass.sharedPreferences.getString("userEmail") ?: "", mainViewModel)
    }

    /**
     * 로딩 애니메이션
     */
    private fun loadingStart() = with(binding) {
        lottieLoading.apply {
            visibility = View.VISIBLE
            playAnimation()
        }
    }

    private fun loadingStop() = with(binding) {
        lottieLoading.apply {
            visibility = View.GONE
            pauseAnimation()
        }
    }

    private fun initBtn() = with(binding) {
        lottieWalk.playAnimation()

        // 산책
        lottieWalk.setOnClickListener {
            mainActivity.changeFragment("walk")
        }

        // 검색
        ivSearch.setOnClickListener {
            mainActivity.changeFragment("search")
        }

        // 알림
        ivNoti.setOnClickListener {
            mainViewModel.requestNotification()
            mainActivity.changeFragment("notification")
        }
    }

    private fun initObserver() = with(boardViewModel) {
        // 전체 피드 조회
        initSelectedBoardList()
        selectedBoardList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                // 피드 전체 조회 실패
                Log.d(TAG, "initObserver: 피드 전체 조회 실패")
            } else {
                // 피드 전체 조회 성공
                boardAdapter.setBoards(it)

                // 로딩 스탑
                loadingStop()
            }
        }

        // 랜덤 큐레이션 List
        curationViewModel.randomCurationList.observe(viewLifecycleOwner) {
            Log.d(TAG, "getRandomCurationList Observer: ${it.size}")

            if (it.isNotEmpty()) {
                homeCurationAdapter.setCurations(it)
                initViewPager()
            }
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
                commentDialog.addComment(it)
            }
            commentDialog.clearEditText()
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

        // 출석 체크 결과
        mainViewModel.initResultAttendance()
        mainViewModel.resultAttendance.observe(viewLifecycleOwner) {
            Log.d(TAG, "initObserver resultAttendance: $it")
            if (it) { // true : 출석체크 아직 안했다면 -> 출석체크 했다고 다일로그 띄움
                val dialog = AttendanceDialog(mainActivity)
                dialog.show()
            }
        }
    }

    // ViewPager 초기 설정
    private fun initViewPager() = with(binding) {
        // 무한 스크롤을 위해 HomeCurationAdapter 내 리스트의 맨앞, 맨뒤에 반대 방향 값 추가하여 실제 데이터는 1부터 시작
        vpCuration.setCurrentItem(1, false)
        vpCuration.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                ciCuration.animatePageSelected((vpCuration.currentItem + curationViewModel.randomCurationList.value!!.size - 1) % curationViewModel.randomCurationList.value!!.size)
                when (vpCuration.currentItem) {
                    homeCurationAdapter.itemCount - 1 -> vpCuration.setCurrentItem(1, false)
                    0 -> vpCuration.setCurrentItem(homeCurationAdapter.itemCount - 2, false)
                }

                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        if (!curationJob.isActive) curationJobCreate()
                    }
                    ViewPager2.SCROLL_STATE_DRAGGING -> curationJob.cancel()
                }
            }
        })

        // ViewPager 하단 위치 표시 점
        ciCuration.createIndicators(curationViewModel.randomCurationList.value!!.size, 0)
    }

    // 일정 시간마다 자동으로 큐레이션 이동
    private fun curationJobCreate() {
        curationJob = lifecycleScope.launchWhenResumed {
            delay(CURATION_JOB_DELAY)
            binding.vpCuration.setCurrentItem(binding.vpCuration.currentItem % curationViewModel.randomCurationList.value!!.size + 1, true)
        }
    }
}
