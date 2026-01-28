package com.imagination.canvaspractice.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.imagination.canvaspractice.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val backStack = mutableStateListOf<Screen>(Screen.Splash) // start destination
}