package com.petmily.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class CheckPermission() {

    fun requestStoragePermission(): Boolean {
        TedPermission.create() // TedPermission 라이브러리를 사용하여 권한을 요청
            .setPermissionListener(object : PermissionListener { // 권한 요청 결과를 처리하는 리스너를 설정
                override fun onPermissionGranted() {
                    // 권한이 허용된 경우 실행
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    // 권한이 거부된 경우
                }
            })
            .setDeniedMessage("권한을 허용해주세요.") // 권한이 거부되었을 때 보여줄 메시지
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE) // 요청할 권한을 설정. 여기서는 외부 저장소 읽기 권한을 요청
            .check() // 권한 요청 실행

        return true
    }

    fun hasStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
