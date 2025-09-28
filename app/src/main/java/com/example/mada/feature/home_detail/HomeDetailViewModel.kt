package com.example.mada.feature.home_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DX"

@HiltViewModel
class HomeDetailViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<HomeDetailAction> = MutableSharedFlow()
    val action: SharedFlow<HomeDetailAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<HomeDetailState> = MutableStateFlow(HomeDetailState())
    val state: StateFlow<HomeDetailState> get() = _state.asStateFlow()

}

data class HomeDetailState(
    val dataSomething: String = "",
)

sealed interface HomeDetailAction

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}