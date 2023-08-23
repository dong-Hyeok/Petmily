package com.petmily.repository.dto

data class PointLog(
    var pointLog: Long = 0L,
    var pointContent: String = "",
    var pointType: Boolean = false,
    var pointCost: Long = 0L,
    var pointUsageDate: String = "",
)

/*
    "pointId": 0,
    "pointContent": "string",
    "pointType": true,
    "pointCost": 0,
    "pointUsageDate": "2023-08-12T19:14:56.012Z"
 */
