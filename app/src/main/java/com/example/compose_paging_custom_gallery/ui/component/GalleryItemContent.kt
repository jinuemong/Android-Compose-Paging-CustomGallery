package com.example.compose_paging_custom_gallery.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import com.example.compose_paging_custom_gallery.domain.CroppingImage
import com.example.compose_paging_custom_gallery.domain.GalleryImage


// 이미지 아이템
@Composable
fun GalleryItemContent(
    galleryImage: GalleryImage,
    selectedImages : List<CroppingImage>,
    setModifyingImage : (GalleryImage) -> Unit,
    removeSelectedImage : (Long) -> Unit
) {
    val isSelected = selectedImages.find { it.id == galleryImage.id} != null

    Box{
//        SubcomposeAsyncImage
    }
}