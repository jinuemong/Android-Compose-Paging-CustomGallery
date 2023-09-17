package com.example.compose_paging_custom_gallery.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose_paging_custom_gallery.domain.CroppingImage


// 갤리러 상단 탑 바
@Composable
fun GalleryTopBar(
    selectedImages : List<CroppingImage>,
    popBackStage : () -> Unit,
) {
    var isDropDownMenuExpanded by remember { mutableStateOf(false) }
    val nothingSelected = selectedImages.isEmpty()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(20.dp,10.dp)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "취소",
            color = Color.White,
            modifier = Modifier.clickable {
                popBackStage()
            }
        )

        // 갤러리 선택
    }
}