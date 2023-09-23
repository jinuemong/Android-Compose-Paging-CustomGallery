package com.example.compose_paging_custom_gallery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rememberView = remember{ mutableStateOf(false) }
            val appState = rememberApplicationState()

            // 비트맵 선정 결과 보내기
            // https://github.com/dlgocks1/Cocktaildakk-Compose/blob/8d6339607e9a6b49c7f028359358527d4b04159f/app/src/main/java/com/compose/cocktaildakk_compose/ui/detail/view/review/ReviewWritingScreen.kt#L31
            val secondScreenResult = appState.navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<List<CroppingImage>>("bitmap_images")
                ?.observeAsState()

            // bottom bar state 초기화
            val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
            ManageBottomBarState(
                navBackStackEntry = navBackStackEntry,
                bottomBarState = appState.bottomBarState
            )
            // 권한 얻기
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ){ isGranted : Boolean ->
                rememberView.value = isGranted
            }
            SideEffect{
                galleryPermissionCheck(context = this@MainActivity, launcher, action = {
                    rememberView.value = true
                })
            }
            if (rememberView.value) {
                GalleryScreen(appState = appState)
            }
        }
    }

    fun galleryPermissionCheck(
        context : Context,
        launcher: ManagedActivityResultLauncher<String,Boolean>,
        action : () -> Unit
    ){
        when(PackageManager.PERMISSION_GRANTED){
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ),
            -> {
                action()
            }
            else -> {
                launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}