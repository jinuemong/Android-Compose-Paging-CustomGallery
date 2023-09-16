package com.example.compose_paging_custom_gallery.domain

// 기기 내부에서 이미지를 가져오기 위해서 ContentResolver를 활용
// 해당 ContentResolver는 Context 객체로부터 할당 받을 수 있음
// Hilt를 활용해서 @ApplicationContext를 통해 ContentResolver를 위임 받을 수 있다.

interface ImageRepository {
    fun getAllPhotos(
        page : Int,
        loadSize : Int,
        currentLocation : String? = null,
    ): MutableList<GalleryImage>

    fun getFolderList() : ArrayList<String>
}