package com.example.compose_paging_custom_gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.compose_paging_custom_gallery.ui.CustomImageCropView
import com.example.compose_paging_custom_gallery.ui.GalleryTopBar
import com.example.compose_paging_custom_gallery.ui.component.GalleryItemContent
import com.example.compose_paging_custom_gallery.ui.component.SelectedImages
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
        GalleryTopBar(
            // 확인 클릭 동작
            confirmCropImages = {
              appState.navController.previousBackStackEntry
                  ?.savedStateHandle
                  ?.set(
                      "bitmap_images",
                      viewModel.selectedImages.toList(),
                  )
            },
            selectedImages = viewModel.selectedImages,
            popBackStage = {
                appState.navController.popBackStack()
            },
            currentDirectory = viewModel.currentFolder.value,
            directories = viewModel.folders,
            setCurrentDirectory = { folder ->
                viewModel.setCurrentFolder(folder)
            },
        )

        SelectedImages(
            selectedImages =  viewModel.selectedImages ,
            removeSelectedImage = { id ->
                viewModel.removeSelectedImage(id)
            }
        )

        CustomImageCropView(
            modifyingImage = viewModel.modifyingImage.value,
            selectedImages = viewModel.selectedImages,
            selectedStatus = viewModel.cropStatus.value,
            setCropStatus = { status ->
                viewModel.setCropStatus(status)
            },
            addSelectedImage = { id, bitmap ->
                viewModel.addSelectedImage(id, bitmap)
            },
            setSelectImages = { index, croppingImage ->
                viewModel.selectedImages[index] = croppingImage
            }
        )

        if (pagingItems.itemCount == 0){
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = "이미지가 존재하지 않습니다.",
                    fontSize = 19.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.DarkGray),
                columns = GridCells.Fixed(4),
            ){
                items(pagingItems.itemCount){ index ->
                    pagingItems[index]?.let { galleryImage ->
                        GalleryItemContent(
                            galleryImage = galleryImage,
                            selectedImages = viewModel.selectedImages,
                            setModifyingImage = {// 이미지 셋
                            },
                            removeSelectedImage ={// 이미지 삭제

                            }
                        )
                    }
                }
            }
        }

    }
}
