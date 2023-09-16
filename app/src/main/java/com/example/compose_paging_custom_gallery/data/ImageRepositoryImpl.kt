package com.example.compose_paging_custom_gallery.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.example.compose_paging_custom_gallery.domain.GalleryImage
import com.example.compose_paging_custom_gallery.domain.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

// ImageRepository 기능 구현
class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context : Context
) : ImageRepository {

    // 버전에 따라서 다른 대응을 해야 한다.
    // 버전 Q는 API 29 이상을 의미하며, 이 경우 접근 uri를 다르게 해야 한다.
    // -> VOLUME_EXTERNAL
    // 그 외의 경우는 content uri를 반환한다
    private val uriExternal : Uri by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

    // 가져올 데이터 형식
    // 이 외에 이미지 크기, 추가 날짜를 얻을 수 있다.
    // projection = null로 지정할 경우 모든 Column을 반환한다.
    private val projection = arrayOf(
        MediaStore.Images.ImageColumns.DATA, // data
        MediaStore.Images.ImageColumns.DISPLAY_NAME, // name
        MediaStore.Images.ImageColumns.DATE_TAKEN, // data token
        MediaStore.Images.ImageColumns._ID // id number,
    )

    // 이미지 정렬 기준을 정의한다.
    // Date Taken 순서로 정렬할 것을 명시한다.
    private val sortedOrder = MediaStore.Images.ImageColumns.DATE_TAKEN

    // ContentResolver를 활용해서 기기 내부 이미지를 가져올 수 있다.
    // 이는 context 객체로 부터 할당 받을 수 있으며,
    // ImageRepositoryImpl에서는 @ApplicationContext를 통해서 ContentResolver를 위임받고 있다.
    private val contentResolver by lazy {
        context.contentResolver
    }

    override fun getAllPhotos(
        page: Int,
        loadSize: Int,
        currentLocation: String?
    ): MutableList<GalleryImage> {
        val galleryImageList = mutableListOf<GalleryImage>()

        // 모든 기기 내 사진을 탐색한다.
        // selection : 반환할 행을 지정한다. SQL WHERE 절로 자동 포맷되며,
        // null로 지정할 경우 모든 URI의 행을 반환한다.
        var selection : String? = null
        // selectionArgs : 선택될 항목을 지정하는 역할을 한다.
        var selectionArgs : Array<String>? = null
        if (currentLocation != null){
            // CurrentLocation이 null이 아니라면 현재 내부 사진을 탐색한다
            selection = "${MediaStore.Images.Media.DATA} LIKE ?"
            selectionArgs = arrayOf("%$currentLocation%")
        }

        val limit = loadSize // 한 번에 불러올 페이징 사이즈
        val offset = (page - 1) * loadSize // 초기 시작 위치를 지정한다.
        // 제작한 query 함수를 사용해서 Cursor를 리턴 받는다.
        val query = getQuery(offset,limit,selection,selectionArgs)

        // query에서 넘어온 query가 null이 아닐 경우 데이터를 하나 씩
        // galleryImageList에 추가한다,
        query?.use{ cursor->
            while (cursor.moveToNext()){
                val id =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                val filePath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                val date =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN))
                val contentUri = ContentUris.withAppendedId(uriExternal, id)
                val image = GalleryImage(
                    id = id,
                    filePath = filePath,
                    uri = contentUri,
                    name = name,
                    date = date ?: "",
                    size = 0,
                )
                galleryImageList.add(image)
            }
        }
        return galleryImageList
        // ContentProvider를 활용해서 galleryImageList를 리턴한다.
        // 이 데이터는 GalleryPagingSource에서 받을 수 있다.
    }

    // 현재 존재하는 폴더 리스트를 가져올 수 있다.
    // contentResolver를 활용하며, 이미지를 가져오는 형식과 비슷하다.
    // 이 폴더 리스트를 ViewModel에 저장한 후, ImagePager를 가져올 때 해당 위치를 넣어서
    // 폴더에 해당하는 이미지를 불러올 수 있다.
    override fun getFolderList(): ArrayList<String> {
        val folderList = ArrayList<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor!=null){
            while (cursor.moveToNext()){
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val filePath = cursor.getString(columnIndex)
                val folder = File(filePath).parent
                if (!folderList.contains(folder)) {
                    folderList.add(folder)
                }
            }
            cursor.close()
        }
        return folderList
    }

    // 현재 파라미터를 사용해서 쿼리를 반환하는 함수이다.
    // Android 버전에 따라서 다르게 반환해야 하며,
    // Build.VERSION_CODES.Q인 API 29 이상을 대응해야 한다.
    // 29 이상과 그 외의 경우를 지정하여 쿼리를 반환한다.
    private fun getQuery(
        offset: Int,
        limit: Int,
        selection: String?,
        selectionArgs: Array<String>?,
    ) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        val bundle = bundleOf(
            ContentResolver.QUERY_ARG_OFFSET to offset,
            ContentResolver.QUERY_ARG_LIMIT to limit,
            ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED),
            ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
            ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
            ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to selectionArgs,
        )
        contentResolver.query(uriExternal, projection, bundle, null)
    } else {
        contentResolver.query(
            uriExternal,
            projection,
            selection,
            selectionArgs,
            "$sortedOrder DESC LIMIT $limit OFFSET $offset",
            )
    }

}