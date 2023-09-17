package com.example.compose_paging_custom_gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.compose_paging_custom_gallery.ui.domain.ApplicationState

@Composable
fun GalleryScreen(
    appState : ApplicationState,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    // State 변환해서 적용
    // collectAsLazyPagingItems 사용 -> 페이징 리스트를 넣으면, 별도 어댑터 없이
    // 자동으로 페이징 리스트 생성
    val pagingItems = viewModel.customGalleryPhotoList.collectAsLazyPagingItems()

    // LaunchedEffect 설명
    // https://onlyfor-me-blog.tistory.com/758
    // 컴포즈 내에서 안전하게 정지 함수를 호출 (코루틴)
    // 컴포지션을 종료하면 취소
    LaunchedEffect(viewModel.currentFolder.value){
        viewModel.getGalleryPagingImages()
    }

    Column(
        modifier = Modifier.background(Color.DarkGray)
    ) {

    }
}
