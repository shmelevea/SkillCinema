package com.example.homework.presentation.basic.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FullScreenImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val imageUrl: LiveData<String> = savedStateHandle.getLiveData("imageUrl")
}