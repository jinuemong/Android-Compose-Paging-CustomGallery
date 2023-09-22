package com.example.compose_paging_custom_gallery.ui.domain

enum class ImageCropStatus {
    WAITING,
    MODIFYING,
    CROPPING,
    ;

    fun isModifying(action : () -> Unit){
        if (this == MODIFYING){
            action()
        }
    }

    fun isCropping(action: () -> Unit){
        if (this == CROPPING){
            action()
        }
    }
}