package com.petmily.repository.api.board

import com.petmily.repository.dto.Comment
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApi {
    @POST("/comment/save")
    suspend fun commentSave(
        @Body body: Comment,
    ): Comment
    
    @DELETE("/comment/{commentId}")
    suspend fun commentDelete(
        @Path("commentId") commentId: Long,
    )
}
