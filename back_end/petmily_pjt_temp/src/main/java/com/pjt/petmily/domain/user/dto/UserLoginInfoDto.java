package com.pjt.petmily.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginInfoDto {
    String userEmail;
    String userToken;
    String userNickname;
    String userProfileImg;
    String userLikePet;
    String userBadge;
    String userRing;
    String userBackground;
    Long userLoginDate;
    Boolean userIsSocial;
    LocalDateTime userAttendance;
}
