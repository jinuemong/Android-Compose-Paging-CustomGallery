package com.example.compose_paging_custom_gallery.utils

import androidx.navigation.NavBackStackEntry
import com.example.compose_paging_custom_gallery.domain.CroppingImage

fun getCroppedImageFromBackStack(navBackStackEntry: NavBackStackEntry?) =
    navBackStackEntry?.savedStateHandle?.getLiveData<List<CroppingImage>>(BITMAP_IMAGES)?.value