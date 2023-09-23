package com.example.compose_paging_custom_gallery

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.compose_paging_custom_gallery.domain.CroppingImage
import com.example.compose_paging_custom_gallery.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.livedata.observeAsState

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rememberView = remember{ mutableStateOf(false) }
            val appState = rememberApplicationState()

            val secondScreenResult = appState.navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<List<CroppingImage>>("bitmap_images")
                ?.observeAsState()

            val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
            ManageBottomBarState(
                navBackStackEntry = navBackStackEntry,
                bottomBarState = appState.bottomBarState
            )
            // 권한 얻는 부분 수정
//            val launcher = rememberLauncherForActivityResult(
//                ActivityResultContracts.RequestPermission(),
//            ){ isGranted : Boolean ->
//                rememberView.value = isGranted
//
//            }
//            launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (rememberView.value) {
                GalleryScreen(appState = appState)
            }
        }
    }
}