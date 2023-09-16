package com.example.compose_paging_custom_gallery

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    // State 변환해서 적용
    // collectAsLazyPagingItems 사용 -> 페이징 리스트를 넣으면, 별도 어댑터 없이
    // 자동으로 페이징 리스트 생성
    val pagingItems = viewModel.customGalleryPhotoList.collectAsLazyPagingItems()

}
