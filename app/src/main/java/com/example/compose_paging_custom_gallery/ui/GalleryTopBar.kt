package com.example.compose_paging_custom_gallery.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose_paging_custom_gallery.R
import com.example.compose_paging_custom_gallery.domain.CroppingImage


// 갤리러 상단 탑 바
@Composable
fun GalleryTopBar(
    selectedImages : List<CroppingImage>,
    popBackStage : () -> Unit,
    currentDirectory : Pair<String,String?>,
    directories : List<Pair<String,String?>>,
    setCurrentDirectory : (Pair<String,String?>) -> Unit,
    confirmCropImages : () -> Unit
) {
    var isDropDownMenuExpanded by remember { mutableStateOf(false) }
    // 선택 된 이미지 없음
    val nothingSelected = selectedImages.isEmpty()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(20.dp, 10.dp)
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    isDropDownMenuExpanded = true
                }
        ){
            Text(
                text = currentDirectory.first,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                modifier = Modifier
                    .rotate(if (isDropDownMenuExpanded) 180f else 0f) // 회전
                    .size(32.dp),
                contentDescription = null,
                tint = Color.White
            )
        }
        // 갤러리 선택 메뉴
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .border(
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                ),
            expanded = isDropDownMenuExpanded,
            onDismissRequest = { isDropDownMenuExpanded = false}
        ) {
            directories.map{
                DropdownMenuItem(onClick = {
                    isDropDownMenuExpanded =false
                    setCurrentDirectory(it)
                }) {
                    Text(
                        text = it.first,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Text(
            text = "확인",
            color = if (nothingSelected) Color.Gray.copy(alpha = 70f) else Color.White,
            modifier = Modifier.clickable {
                if (!nothingSelected){
                    confirmCropImages() // 선택 이미지 픽
                }
            }
        )
    }
}