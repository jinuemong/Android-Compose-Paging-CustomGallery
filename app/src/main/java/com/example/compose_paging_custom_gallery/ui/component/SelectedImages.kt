package com.example.compose_paging_custom_gallery.ui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose_paging_custom_gallery.domain.CroppingImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import com.example.compose_paging_custom_gallery.R

@Composable
fun SelectedImages(
    selectedImages : SnapshotStateList<CroppingImage>,
    removeSelectedImage : (Long) -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(
        Modifier
            .fillMaxWidth(1f)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
    ) {
        Text(
            text = "선택된 이미지 ${selectedImages.size} / 5",
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            selectedImages.map {
                Box{
                    Image(
                        painter = rememberImagePainter(it.croppedBitmap),
                        contentDescription = null,
                        modifier = Modifier
                            .size(screenWidth.div(6))
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                Color.DarkGray
                            )
                            .align(Alignment.TopEnd)
                            .clickable {
                                removeSelectedImage(it.id)
                            },
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "이미지 취소",
                        tint = Color.White
                    )
                }
            }

        }
    }
}