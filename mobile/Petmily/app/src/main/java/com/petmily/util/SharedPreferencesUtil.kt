package com.petmily.util

import android.content.Context
import android.content.SharedPreferences
import com.petmily.config.ApplicationClass
import com.petmily.config.ApplicationClass.Companion.REFRESH_TOKEN
import com.petmily.config.ApplicationClass.Companion.X_ACCESS_TOKEN
import com.petmily.repository.dto.Pet
import com.petmily.repository.dto.UserLoginInfoDto

class SharedPreferencesUtil(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences(ApplicationClass.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    fun getLong(key: String): Long {
        return preferences.getLong(key, 0L)
    }

    fun isWalking(): Boolean {
        return preferences.getBoolean("isWalking", false)
    }

    fun startWalk(pet: Pet) {
        preferences.edit().apply {
            putBoolean("isWalking", true)
            putString("petName", pet.petName)
            putLong("petId", pet.petId)
            apply()
        }
    }

    fun stopWalk() {
        preferences.edit().apply {
            putBoolean("isWalking", false)
            putString("petName", "")
            putLong("petId", 0L)
            apply()
        }
    }

    fun addUser(userLoginInfoDto: UserLoginInfoDto) {
        preferences.edit().apply {
            putLong("userId", userLoginInfoDto.userId)
            putString("userEmail", userLoginInfoDto.userEmail)
            putString("userNickname", userLoginInfoDto.userNickname)
            putString("userProfileImg", userLoginInfoDto.userProfileImg)
            putString(REFRESH_TOKEN, userLoginInfoDto.userToken)
            apply()
        }
    }

    fun addAccessToken(token: String) {
        preferences.edit().apply {
            putString(X_ACCESS_TOKEN, token)
            apply()
        }
    }

    fun removeUser() {
        preferences.edit().clear().apply()
    }
}
