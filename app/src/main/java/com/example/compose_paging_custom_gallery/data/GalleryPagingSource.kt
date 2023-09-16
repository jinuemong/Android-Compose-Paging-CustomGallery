package com.example.compose_paging_custom_gallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.compose_paging_custom_gallery.domain.GalleryImage
import com.example.compose_paging_custom_gallery.domain.ImageRepository

// 래포지토리로 부터 이미지를 불러와서 페이징 결과를 반환
// LoadResult 형식으로 반환
class GalleryPagingSource(
    private val imageRepository : ImageRepository,
    private val currentLocation : String?
) : PagingSource<Int, GalleryImage>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImage> {
        return try{
            val position = params.key ?: STARTING_PAGE_INDEX
            val data = imageRepository.getAllPhotos(
                page = position, // start page position
                loadSize = params.loadSize, // load size
                currentLocation = currentLocation // current location -> 현재 폴더 위치
            )
            val endOfPaginationReached = data.isEmpty()
            // 이전 key, 첫번째 페이지인 경우 null 반환, else -> position - 1
            val prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1
            // 다음 key, 마지막 페이지인 경우 null 반환, else -> 현재 position
            // +  다음 로드 횟수 (전체 로드 사이즈 / 한 번에 로드 사이즈) (페이지 수 )
            val nextKey =
                if (endOfPaginationReached) null else position + (params.loadSize / PAGING_SIZE)
            // data를 시작 - 끝 key 만큼 가져옴
            LoadResult.Page(data,prevKey,nextKey)
        } catch (e : Exception){
            // 로드 실패 시 에러 반환
            LoadResult.Error(e)
        }
    }

    // 현재 포지셔닝을 카운터해서, key를 리셋한다.
    // 새로 데이터를 불러와야 할 경우를 명시
    override fun getRefreshKey(state: PagingState<Int, GalleryImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
        const val PAGING_SIZE = 28
    }
}