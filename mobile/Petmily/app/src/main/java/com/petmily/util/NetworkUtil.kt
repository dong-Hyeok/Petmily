package com.petmily.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkUtil {
    companion object {
        // 인터넷 연결 상태를 확인하는 함수
        fun isInternetConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            } else {
                try {
                    if (connectivityManager.activeNetworkInfo == null) {
                        false
                    } else {
                        connectivityManager.activeNetworkInfo?.isConnected!!
                    }
                } catch (e: Exception) {
                    false
                }
            }
        }
    }
}
