package com.pjt.petmily.domain.sns.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BoardRequestDto {

    private String userEmail;
    private String boardContent;
}
