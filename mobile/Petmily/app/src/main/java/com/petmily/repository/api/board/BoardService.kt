package com.petmily.repository.api.board

import android.util.Log
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.HashTagRequestDto
import com.petmily.repository.dto.UserLoginInfoDto
import com.petmily.util.RetrofitUtil
import okhttp3.MultipartBody

private const val TAG = "Fetmily_BoardService"
class BoardService {
    /**
     * 피드 등록
     */
    suspend fun boardSave(file: List<MultipartBody.Part>?, board: Board, hashTagRequestDto: HashTagRequestDto): Boolean {
        return try {
            RetrofitUtil.boardApi.boardSave(file, board, hashTagRequestDto)
            true
        } catch (e: Exception) {
            Log.d(TAG, "boardSave: ${e.message}")
            false
        }
    }

    /**
     * 피드 수정
     */
    suspend fun boardUpdate(boardId: Long, file: List<MultipartBody.Part>?, board: Board, hashTagRequestDto: HashTagRequestDto): Boolean {
        return try {
            RetrofitUtil.boardApi.boardUpdate(boardId, file, board, hashTagRequestDto)
            true
        } catch (e: Exception) {
            Log.d(TAG, "boardUpdate: ${e.message}")
            false
        }
    }

    /**
     * 피드 삭제
     */
    suspend fun boardDelete(boardId: Long, userLoginInfoDto: UserLoginInfoDto): Boolean {
        return try {
            RetrofitUtil.boardApi.boardDelete(boardId, userLoginInfoDto)
            true
        } catch (e: Exception) {
            Log.d(TAG, "boardDelete: ${e.message}")
            false
        }
    }

    /**
     * 피드 전체 조회
     */
    suspend fun boardSelectAll(userEmail: String): List<Board> {
        return try {
            RetrofitUtil.boardApi.boardSelectAll(userEmail)
        } catch (e: Exception) {
            Log.d(TAG, "boardSelectAll: ${e.message}")
            listOf()
        }
    }

    /**
     * 피드 단일 조회
     */
    suspend fun boardSelectOne(boardId: Long, currentUserEmail: String): Board {
        return try {
            RetrofitUtil.boardApi.boardSelectOne(boardId, currentUserEmail)
        } catch (e: Exception) {
            Log.d(TAG, "boardSelect: ${e.message}")
            Board()
        }
    }

    /**
     * 좋아요 등록
     */
    suspend fun registerHeart(boardId: Long, userEmail: String) {
        try {
            RetrofitUtil.boardApi.registerHeart(Board(boardId = boardId, userEmail = userEmail))
        } catch (e: Exception) {
            Log.d(TAG, "registerHeart: ${e.message}")
        }
    }

    /**
     * 좋아요 취소
     */
    suspend fun deleteHeart(boardId: Long, userEmail: String) {
        try {
            RetrofitUtil.boardApi.deleteHeart(Board(boardId = boardId, userEmail = userEmail))
        } catch (e: Exception) {
            Log.d(TAG, "registerHeart: ${e.message}")
        }
    }

    /**
     * 게시물 검색
     */
    suspend fun searchBoard(hashTag: String, currentUser: String): List<Board> {
        return try {
            RetrofitUtil.boardApi.searchBoard(hashTag, currentUser)
        } catch (e: Exception) {
            Log.d(TAG, "searchBoard: ${e.message}")
            listOf()
        }
    }
}
