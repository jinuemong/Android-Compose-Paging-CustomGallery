package com.example.compose_paging_custom_gallery.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose_paging_custom_gallery.domain.CroppingImage
import com.example.compose_paging_custom_gallery.domain.GalleryImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.compose_paging_custom_gallery.R
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip

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
        // SubcomposeAsyncImage를 활용해서 에러가 났을 경우 이미지 로드 처리
        // uri 이미지 처리에 도움을 줌
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(galleryImage.uri)
                .crossfade(true)
                .build(),
            loading = {
                ListCircularProgressIndicator(fraction = 0.2f)
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .padding(2.dp)
                .animateContentSize()
                // 이미지 클릭 적용 -> selected
                .clickable {
                    setModifyingImage(galleryImage)
                },
            alpha = if (isSelected) 0.5f else 1f,
            // 로드 실패
            error = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_error_outline_24),
                        contentDescription = "Icon Error",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "지원하지 않는\n파일 형식입니다.",
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            },
        )

        // 선택 뷰
        if (isSelected){
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Color.DarkGray
                    )
                    .align(Alignment.TopEnd)
                    // 이미지 삭제
                    .clickable {
                        removeSelectedImage(galleryImage.id)
                    },
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}