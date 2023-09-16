package com.example.compose_paging_custom_gallery.domain

import android.graphics.Bitmap

data class CroppingImage (
    val id : Long,
    val croppedBitmap : Bitmap,
)