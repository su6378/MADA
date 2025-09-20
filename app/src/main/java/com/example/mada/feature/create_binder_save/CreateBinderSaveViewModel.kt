package com.example.mada.feature.create_CreateBinder_save

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
class CreateBinderSaveViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<CreateBinderSaveAction> = MutableSharedFlow()
    val action: SharedFlow<CreateBinderSaveAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<CreateBinderSaveState> = MutableStateFlow(CreateBinderSaveState())
    val state: StateFlow<CreateBinderSaveState> get() = _state.asStateFlow()

    init {

    }

    fun navigateBinderSaveFragment() = viewModelScope.launch {
        _action.emit(CreateBinderSaveAction.NavigateBinderSaveView)
    }
}

data class CreateBinderSaveState(
    var isSaveAble: Boolean = false,
)

sealed interface CreateBinderSaveAction {
    class ShowToast(val content: String) : CreateBinderSaveAction
    data object NavigateBinderSaveView : CreateBinderSaveAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}