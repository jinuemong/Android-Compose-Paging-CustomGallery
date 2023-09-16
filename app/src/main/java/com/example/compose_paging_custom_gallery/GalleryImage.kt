package com.example.compose_paging_custom_gallery

import android.net.Uri

// 이미지 객체의 정보를 나타낼 클래스이다.
// 정보를 객체에서 가져온 후 isSelected를 추가해서 선택된 이미지를 구분할 수 있게 했다.

data class GalleryImage(
    val id : Long,
    val filePath : String,
    val uri : Uri,
    val name : String,
    val date : String,
    val size : Int,
    val isSelected : Boolean = false,
)