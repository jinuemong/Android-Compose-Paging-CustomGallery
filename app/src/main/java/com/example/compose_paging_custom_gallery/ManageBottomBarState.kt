package com.example.compose_paging_custom_gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavBackStackEntry

@Composable
fun ManageBottomBarState (
    navBackStackEntry: NavBackStackEntry?,
    bottomBarState : MutableState<Boolean>
){
    // bottom navi
    when (navBackStackEntry?.destination?.route){
    }
}