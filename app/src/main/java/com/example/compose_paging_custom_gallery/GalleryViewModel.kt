package com.example.compose_paging_custom_gallery

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.view.WindowInsetsAnimationCompat.Callback.DispatchMode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.compose_paging_custom_gallery.data.GalleryPagingSource
import com.example.compose_paging_custom_gallery.data.GalleryPagingSource.Companion.PAGING_SIZE
import com.example.compose_paging_custom_gallery.di.DispatcherModule
import com.example.compose_paging_custom_gallery.domain.CroppingImage
import com.example.compose_paging_custom_gallery.domain.GalleryImage
import com.example.compose_paging_custom_gallery.domain.ImageRepository
import com.example.compose_paging_custom_gallery.ui.domain.ImageCropStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// PagingSource를 선언하고, 이를 통해서 페이징 된 결과를 받아온다.
// 페이징 결과는 Flow 형태로 반환받기 때문에 StateFlow를 통해서 페이징 결과를 보관한다.
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    // provides 부여
    @DispatcherModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _customGalleryPhotoList =
        MutableStateFlow<PagingData<GalleryImage>>(PagingData.empty())

    val customGalleryPhotoList : StateFlow<PagingData<GalleryImage>> =
        _customGalleryPhotoList.asStateFlow()

    // 이미지 선택 상태
    private val _cropStatus = mutableStateOf(ImageCropStatus.WAITING)
    val cropStatus: State<ImageCropStatus> = _cropStatus


    // 폴더 리스트
    private val _folders = mutableStateListOf<Pair<String,String?>>("최근사진" to null)
    val folders get() = _folders

    // 현재 폴더
    private val _currentFolder = mutableStateOf<Pair<String, String?>>("최근사진" to null)
    val currentFolder : State<Pair<String, String?>> = _currentFolder

    // 현재 이미지
    private val _modifyingImage = mutableStateOf<GalleryImage?>(null)
    val modifyingImage : State<GalleryImage?> = _modifyingImage

    // 선택 이미지 리스트
    private val _selectedImages = mutableStateListOf<CroppingImage>()
    val selectedImages: SnapshotStateList<CroppingImage> = _selectedImages

    // 페이징 처리 함수
    fun getGalleryPagingImages() = viewModelScope.launch {
        _customGalleryPhotoList.value = PagingData.empty()
        Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                GalleryPagingSource(
                    imageRepository = imageRepository,
//                    currentLocation =  null
                    // null -> 모든 위치 사진 가져오기,
                    currentLocation = currentFolder.value.second,
                    // currentFolder.value... -> 해당 위치 사진 가져오기
                )
            },
        ).flow.cachedIn(viewModelScope).collectLatest {
            _customGalleryPhotoList.value = it
        }
    }

    // 현재 폴더 위치 변경
    fun setCurrentFolder(location : Pair<String, String?>){
        _currentFolder.value = location
    }

    // 현재 폴더 리스트 저장
    fun getFolder(){
        imageRepository.getFolderList().map {
            _folders.add(it.split("/").last() to it)
        }
    }

    // 현재 선택 사진 지정
    fun setModifyingImage(image : GalleryImage){
        _modifyingImage.value = image
    }

    // 새 이미지 추가
    fun addSelectedImage(id : Long, image : Bitmap) {
        _selectedImages.add(CroppingImage(id,image))
    }

    // 선택 이미지 삭제
    fun removeSelectedImage(id: Long) {
        val removedImage = _selectedImages.find { it.id == id }
        removedImage?.let {
            _selectedImages.remove(removedImage)
        }
    }

    fun setCropStatus(status: ImageCropStatus) {
        _cropStatus.value = status
    }

    fun addCroppedImage(secondScreenResult: List<CroppingImage>?) {
        secondScreenResult?.let { _selectedImages.addAll(it) }
    }

}