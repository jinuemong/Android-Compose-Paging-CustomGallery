package com.example.compose_paging_custom_gallery

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.compose_paging_custom_gallery.GalleryPagingSource.Companion.PAGING_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _customGalleryPhotoList =
        MutableStateFlow<PagingData<GalleryImage>>(PagingData.empty())

    val customGalleryPhotoList : StateFlow<PagingData<GalleryImage>>
        get() = _customGalleryPhotoList.asStateFlow()

    private val _folders = mutableStateListOf<Pair<String,String?>>("최근사진" to null)
    private val folders get() = _folders

    private val _currentFolder = mutableStateOf<Pair<String, String?>>("최근사진" to null)
    private val currentFolder : State<Pair<String, String?>> = _currentFolder

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
}