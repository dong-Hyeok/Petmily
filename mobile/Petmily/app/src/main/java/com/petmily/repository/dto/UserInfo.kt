package com.petmily.repository.dto

import okhttp3.MultipartBody

data class UserInfo(
    var userLoginInfoDto: UserLoginInfoDto,
    var file: MultipartBody.Part?, // 이미지 파일
)
