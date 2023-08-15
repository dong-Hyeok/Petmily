package com.petmily.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petmily.config.ApplicationClass
import com.petmily.repository.api.board.BoardService
import com.petmily.repository.api.board.CommentService
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.Comment
import com.petmily.repository.dto.HashTagRequestDto
import com.petmily.repository.dto.TokenRequestDto
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.util.TokenExpiredException
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.net.ConnectException

private const val TAG = "petmily_BoardViewModel"
class BoardViewModel : ViewModel() {
    private val boardService by lazy { BoardService() }

    // 피드 입력 시 태그 리스트
    var boardTags = mutableListOf<String>()

    // 피드 등록 통신 결과
    private var _isBoardSaved = MutableLiveData<Boolean>()
    val isBoardSaved: LiveData<Boolean>
        get() = _isBoardSaved

    // 피드 수정 통신 결과
    private var _isBoardUpdated = MutableLiveData<Boolean>()
    val isBoardUpdated: LiveData<Boolean>
        get() = _isBoardUpdated

    // 피드 삭제 통신 결과
    private var _isBoardDeleted = MutableLiveData<Boolean>()
    val isBoardDeleted: LiveData<Boolean>
        get() = _isBoardDeleted

    // 피드 전체 조회 결과
    private var _selectedBoardList = MutableLiveData<List<Board>>()
    val selectedBoardList: LiveData<List<Board>>
        get() = _selectedBoardList

    // 피드 단일 조회 결과
    private var _selectOneBoard = MutableLiveData<Board>()
    val selectOneBoard: LiveData<Board>
        get() = _selectOneBoard

    // 선택된 피드
    var selectedBoard = Board()

    /**
     * API - 피드 등록 통신
     */
    fun saveBoard(file: List<MultipartBody.Part>?, board: Board, hashTagRequestDto: HashTagRequestDto, mainViewModel: MainViewModel) {
        Log.d(TAG, "saveBoard: 피드 등록")
        viewModelScope.launch {
            try {
                _isBoardSaved.value = boardService.boardSave(file, board, hashTagRequestDto)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 피드 수정 통신
     */
    fun updateBoard(boardId: Long, file: List<MultipartBody.Part>?, board: Board, hashTagRequestDto: HashTagRequestDto, mainViewModel: MainViewModel) {
        Log.d(TAG, "updateBoard: 피드 수정")
        viewModelScope.launch {
            try {
                _isBoardUpdated.value = boardService.boardUpdate(boardId, file, board, hashTagRequestDto)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 피드 삭제 통신
     */
    fun deleteBoard(boardId: Long, userLoginInfoDto: UserLoginInfoDto, mainViewModel: MainViewModel) {
        Log.d(TAG, "deleteBoard: 피드 삭제")
        viewModelScope.launch {
            try {
                _isBoardDeleted.value = boardService.boardDelete(boardId, userLoginInfoDto)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 피드 전체 조회 통신
     */
    fun selectAllBoard(userEmail: String, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            try {
                _selectedBoardList.value = boardService.boardSelectAll(userEmail).reversed()
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            } catch (e: TokenExpiredException) {
                // TODO: 액세스 토큰 요청

                mainViewModel.refreshAccessToken(
                    TokenRequestDto(
                        ApplicationClass.sharedPreferences.getString(ApplicationClass.REFRESH_TOKEN) ?: "",
                        ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                    ),
                )
            }
        }
    }

    /**
     * API - 피드 단일 조회 통신
     */
    fun selectOneBoard(boardId: Long, mainViewModel: MainViewModel) {
        Log.d(TAG, "selectBoard: 피드 단일 조회 통신")
        viewModelScope.launch {
            try {
                _selectOneBoard.value = boardService.boardSelectOne(boardId)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    fun initIsBoardSaved() { _isBoardSaved = MutableLiveData<Boolean>() }
    fun initIsBoardUpdated() { _isBoardUpdated = MutableLiveData<Boolean>() }
    fun initIsBoardDeleted() { _isBoardDeleted = MutableLiveData<Boolean>() }

    // ------------------------------------------------------------------------------------------------------------------------
    // Comment
    // ------------------------------------------------------------------------------------------------------------------------

    private val commentService by lazy { CommentService() }

    // 댓글 등록 통신 결과
    private var _commentSaveResult = MutableLiveData<Comment>()
    val commentSaveResult: LiveData<Comment>
        get() = _commentSaveResult

    // 댓글 삭제 통신 결과
    private var _isCommentDeleted = MutableLiveData<Boolean>()
    val isCommentDeleted: LiveData<Boolean>
        get() = _isCommentDeleted

    // 댓글 태그나 삭제 시 선택한 댓글
    var selectedComment = Comment()

    /**
     * API - 댓글 등록 통신
     */
    fun saveComment(comment: Comment, mainViewModel: MainViewModel) {
        Log.d(TAG, "saveComment: 댓글 등록")
        viewModelScope.launch {
            try {
                _commentSaveResult.value = commentService.commentSave(comment)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    /**
     * API - 댓글 삭제 통신
     */
    fun deleteComment(commentId: Long, mainViewModel: MainViewModel) {
        Log.d(TAG, "deleteComment: 댓글 삭제")
        viewModelScope.launch {
            try {
                _isCommentDeleted.value = commentService.commentDelete(commentId)
            } catch (e: ConnectException) {
                mainViewModel.setConnectException()
            }
        }
    }

    fun initCommentSaveResult() { _commentSaveResult = MutableLiveData<Comment>() }
    fun initIsCommentDeleted() { _isCommentDeleted = MutableLiveData<Boolean>() }

    // ------------------------------------------------------------------------------------------------------------------------
    // Heart
    // ------------------------------------------------------------------------------------------------------------------------

    /**
     * API - 좋아요 등록 통신
     */
    fun registerHeart(boardId: Long, userEmail: String) {
        Log.d(TAG, "registerHeart: 좋아요 등록")
        viewModelScope.launch {
            boardService.registerHeart(boardId, userEmail)
        }
    }

    /**
     * API - 좋아요 취소 통신
     */
    fun deleteHeart(boardId: Long, userEmail: String) {
        Log.d(TAG, "deleteHeart: 좋아요 취소")
        viewModelScope.launch {
            boardService.deleteHeart(boardId, userEmail)
        }
    }
}
