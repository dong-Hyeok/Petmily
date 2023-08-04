package com.pjt.petmily.domain.sns.comment.dto;

import java.time.LocalDateTime;

import com.pjt.petmily.domain.sns.comment.Comment;
import com.pjt.petmily.domain.user.User;


public class CommentSaveDto {
    private String commentContent;
    private LocalDateTime commentTime;
    private User user;

    public Comment toEntity(){
        return Comment.builder()
                .commentContent(commentContent)
                .commentTime(LocalDateTime.now())
                .build();
    }
}
