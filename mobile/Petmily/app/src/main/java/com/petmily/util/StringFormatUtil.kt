package com.petmily.util

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "StringFormatUtil"
class StringFormatUtil {
    companion object {
        private var year = 0
        private var month = 0
        private var day = 0
        private var hour = 0
        private var minute = 0

        private var nYear = 0
        private var nMonth = 0
        private var nDay = 0
        private var nHour = 0
        private var nMinute = 0

        @SuppressLint("SimpleDateFormat")
        private fun setNow() {
            SimpleDateFormat("yyyy-MM-dd-hh-mm")
                .format(Date(System.currentTimeMillis()))
                .split("-")
                .apply {
                    nYear = this[0].toInt()
                    nMonth = this[1].toInt()
                    nDay = this[2].toInt()
                    nHour = this[3].toInt()
                    nMinute = this[4].toInt()
                }
        }

        fun uploadDateFormat(date: String): String {
            try {
                setNow()

                year = date.substring(0..3).toInt()
                month = date.substring(5..6).toInt()
                day = date.substring(8..9).toInt()
                hour = date.substring(11..12).toInt()
                minute = date.substring(14..15).toInt()

                Log.d(TAG, "uploadDateFormat: $date / $year / $month / $day / $hour / $minute")

                return if (nYear > year) {
                    "${nYear - year}년 전"
                } else if (nMonth > month) {
                    "${nMonth - month}달 전"
                } else if (nDay > day) {
                    "${nDay - day}일 전"
                } else if (nHour > hour) {
                    "${nHour - hour}시간 전"
                } else if (nMinute > minute) {
                    "${nMinute - minute}분 전"
                } else {
                    "방금 전"
                }
            } catch (e: Exception) {
                Log.d(TAG, "uploadDateFormat: ${e.message}")
                return ""
            }
        }

        fun likeCntFormat(likeCnt: Int): String {
            return if (likeCnt >= 1_000_000) {
                String.format("%.2f M", likeCnt.toFloat() / 1_000_000)
            } else if (likeCnt >= 1_000) {
                String.format("%.2f K", likeCnt.toFloat() / 1_000)
            } else {
                likeCnt.toString()
            }
        }

        /**
         * 현재 시간을 mysql timestamp의 포맷에 맞게 변경
         * 2023-08-14T23:11:38.278Z
         */
        @SuppressLint("SimpleDateFormat")
        fun currentTimeToTimeStamp(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .format(System.currentTimeMillis())
                .replace(" ", "T")
                .plus("Z")
        }

        /**
         * 거리값 포맷
         */
        fun distanceIntToString(dist: Int): String {
            return if (dist >= 1000) {
                "${String.format("%.2f", dist.toFloat() / 1000)} km"
            } else {
                "$dist m"
            }
        }

        /**
         * 소요 시간 포맷
         */
        fun timeIntToString(time: Int): String {
            var str = "${(time / 60) % 60} 분"
            if (time >= 3600) str = "${time / 3600} 시간 " + str
            return str
        }
    }
}
