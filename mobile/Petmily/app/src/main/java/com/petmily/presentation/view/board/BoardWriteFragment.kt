package com.petmily.presentation.view.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.FragmentBoardWriteBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.HashTagRequestDto
import com.petmily.util.CheckPermission
import com.petmily.util.GalleryUtil
import com.petmily.util.UploadUtil
import okhttp3.MultipartBody

private const val TAG = "Fetmily_BoardWriteFrag"
class BoardWriteFragment :
    BaseFragment<FragmentBoardWriteBinding>(FragmentBoardWriteBinding::bind, R.layout.fragment_board_write) {

    private lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()

    private lateinit var galleryUtil: GalleryUtil
    private val uploadUtil by lazy { UploadUtil() }
    private lateinit var checkPermission: CheckPermission

    // image Adapter
    private lateinit var imageAdapter: ImageAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        galleryUtil = GalleryUtil()
        checkPermission = CheckPermission()

        // 피드 작성시 태그 초기화 (GalleryFragment에서 왔을때는 초기화하지 않도록)
        boardViewModel.boardTags.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.bottomNaviVisible()
        initWriteOrUpdate()
        init()
        initView()
        initAdapter()
        initButton()
        initEditText()
        initImageView()
        initObserver()
        initBackPressEvent()
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

    private fun init() = with(binding) {
        mainViewModel.setFromGalleryFragment("addFeedFragment")

        // 글 작성 부분을 최초 포커스로 지정
        etBoardContent.requestFocus()

        // ViewModel에 있는 tag 불러오기
        boardViewModel.boardTags.forEach {
            Log.d(TAG, "init: $it")
            chipGroup.addView(createChip(it))
        }
    }

    private fun initView() = with(binding) {
        clMypageUserImage.setBackgroundColor(Color.parseColor("#ffffff"))

        ApplicationClass.sharedPreferences.apply {
            tvName.text = getString("userNickname")
            Glide.with(mainActivity)
                .load(getString("userProfileImg"))
                .into(ivProfile)
        }
    }

    private fun initWriteOrUpdate() = with(binding) {
        if (boardViewModel.selectedBoard.boardId == 0L) {
            // 게시글 작성 상태
            btnAddBoard.text = "등록"
        } else {
            // 게시글 수정 상태
            boardViewModel.selectedBoard.apply {
                etBoardContent.setText(boardContent)
                boardViewModel.boardTags = hashTags.toMutableList()
            }
            btnAddBoard.text = "수정"
        }
    }

    private fun initAdapter() = with(binding) {
        imageAdapter = ImageAdapter()

        rcvSelectImage.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initButton() = with(binding) {
        // 등록 및 수정 버튼
        btnAddBoard.setOnClickListener {
            if (isValidInput() && mainActivity.isNetworkConnected()) {
                val files: ArrayList<MultipartBody.Part> = arrayListOf()

                if (!mainViewModel.addPhotoList.value.isNullOrEmpty()) {
                    // addPhotoList에 추가되어 있는 파일 전송
                    mainViewModel.addPhotoList.value!!.forEach {
                        Log.d(TAG, "addPhoto: ${it.imgUrl}")

                        // filePath -> MultipartBody.Part 생성
                        // 여기서 두번째 파라미터 "file"은 api 통신 상의 key값
                        val multipartData = uploadUtil.createMultipartFromUri(mainActivity, "file", it.imgUrl)!!
                        files.add(multipartData)
                    }
                }

                val board = Board(
                    boardContent = etBoardContent.text.toString(),
                    userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                )

                Log.d(TAG, "첨부한 이미지 수: ${files.size}")
                if (boardViewModel.selectedBoard.boardId == 0L) {
                    boardViewModel.saveBoard(files, board, HashTagRequestDto(boardViewModel.boardTags), mainViewModel)
                } else {
                    boardViewModel.updateBoard(boardViewModel.selectedBoard.boardId, files, board, HashTagRequestDto(boardViewModel.boardTags), mainViewModel)
                }
            }
        }
    }

    private fun initImageView() = with(binding) {
        // 이미지 추가 버튼 (N장)
        ivSelectImage.setOnClickListener {
            if (checkPermission.hasStoragePermission(mainActivity)) {
                // 이미 권한이 획득된 경우의 처리
                if (galleryUtil.getImages(mainActivity, mainViewModel)) { // 갤러리 이미지를 모두 로드 했다면
                    mainActivity.changeFragment("gallery")
                }
            } else {
                // 권한이 획득되지 않은 경우 권한 요청
                checkPermission.requestStoragePermission()
            }
        }
    }

    private fun initEditText() = with(binding) {
        // 태그
        etPetName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addChipsFromEditText()
                true
            } else {
                false
            }
        }
    }

    private fun initObserver() = with(boardViewModel) {
        // 게시물 등록 결과
        initIsBoardSaved()
        isBoardSaved.observe(viewLifecycleOwner) {
            if (!it) {
                // 게시물 등록 실패
                mainActivity.showSnackbar("게시물 등록에 실패하였습니다.")
            } else {
                // 게시물 등록 성공
                mainActivity.showSnackbar("게시물이 등록되었습니다.")
                mainActivity.changeFragment("home")
            }
        }

        // 게시물 수정 결과
        initIsBoardUpdated()
        isBoardUpdated.observe(viewLifecycleOwner) {
            if (!it) {
                // 게시물 수정 실패
                mainActivity.showSnackbar("게시물 수정에 실패하였습니다.")
            } else {
                // 게시물 수정 성공
                mainActivity.showSnackbar("게시물이 수정되었습니다.")
                mainActivity.changeFragment("home")
            }
        }

        mainViewModel.addPhotoList.observe(viewLifecycleOwner) {
            // 선택한 사진이 비어있지 않으면 -> 리사이클러 뷰에 사진 적제
            if (!it.isNullOrEmpty()) {
                imageAdapter.setImgs(it)
            }
        }
    }

    private fun addChipsFromEditText() = with(binding) {
        val newText = etPetName.text.toString().trim()
        if (newText.isNotBlank() && !boardViewModel.boardTags.contains(newText)) {
            val tags = newText.split("#").filter { it.isNotBlank() }
            for (tag in tags) {
                val chip = createChip(tag)
                chipGroup.addView(chip)
                boardViewModel.boardTags.add(tag)
            }
        }
        // EditText 초기화
        etPetName.text?.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun createChip(tag: String): Chip = with(binding) {
        val chip = Chip(mainActivity, null, R.style.CustomChip)
        chip.apply {
            text = "#$tag"
            setChipBackgroundColorResource(R.color.main_color)
            setTextColor(ContextCompat.getColor(mainActivity, R.color.white))

            isCloseIconVisible = true // x 아이콘을 버튼을 보이게 설정
            setOnCloseIconClickListener {
                chipGroup.removeView(chip) // 클릭 시 Chip을 삭제합니다.
                boardViewModel.boardTags.remove(tag)
            }
        }

        return chip
    }

    /**
     * 게시물 등록 관련 입력이 유효한가
     */
    private fun isValidInput(): Boolean = with(binding) {
        return if (etBoardContent.text.isNullOrBlank()) {
            mainActivity.showSnackbar("내용을 입력해주세요.")
            false
        } else {
            true
        }
    }
}
