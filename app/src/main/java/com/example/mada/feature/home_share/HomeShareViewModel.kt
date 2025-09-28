package com.example.mada.feature.home_share

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
class HomeShareViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<HomeShareAction> = MutableSharedFlow()
    val action: SharedFlow<HomeShareAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<HomeShareState> = MutableStateFlow(HomeShareState())
    val state: StateFlow<HomeShareState> get() = _state.asStateFlow()

    fun saveHomeImage() = viewModelScope.launch {
        _action.emit(HomeShareAction.SaveHomeImage)
    }

    fun shareHomeImage() = viewModelScope.launch {
        _action.emit(HomeShareAction.ShareHomeImage)
    }
}

data class HomeShareState(
    val dataSomething: String = "",
)

sealed interface HomeShareAction {
    data object ShareHomeImage : HomeShareAction
    data object SaveHomeImage: HomeShareAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}