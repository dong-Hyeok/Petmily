package com.petmily.util

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.repository.dto.Photo
private const val TAG = "petmily_GalleryUtil"
class GalleryUtil {

    fun getImages(context: Context, mainViewModel: MainViewModel): Boolean {
        // 사진 선택을 위해 이전에 선택했던 사진들은 모두 초기화
        mainViewModel.emptyGalleryList()

        // 가져올 이미지의 속성을 지정. 여기서는 이미지 ID, 이미지 파일 이름, 이미지 파일 경로를 가져옴
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
        )

        // 이미지를 추가된 날짜의 역순으로 정렬하여 최신 이미지부터 가져옴
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 외부 저장소의 이미지 컨텐츠를 가리키는 URI를 사용하여 이미지 정보를 가져옴
            projection,
            null,
            null,
            sortOrder,
        )

        // 커서로 앨범의 이미지를 하나씩 addToGalleryList에 저장
        cursor?.use { cursor ->
            val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(idColumnIndex)
                val imageName = cursor.getString(nameColumnIndex)
                val imageData = cursor.getString(dataColumnIndex)
                Log.d("gallery", "loadPhotos: $imageName")
                // 이미지 데이터를 사용하여 필요한 작업 수행
                mainViewModel.addToGalleryList(
                    Photo().apply {
                        imgUrl = imageData
                        isSelected.value = false
                    },
                )
            }
        }

        return true
    }
}
