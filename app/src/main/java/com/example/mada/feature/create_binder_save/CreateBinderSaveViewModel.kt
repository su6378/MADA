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

    private val _state: MutableStateFlow<CreateBinderSaveState> =
        MutableStateFlow(CreateBinderSaveState())
    val state: StateFlow<CreateBinderSaveState> get() = _state.asStateFlow()

    fun showCreateSaveBinderDialog() = viewModelScope.launch {
        _action.emit(CreateBinderSaveAction.ShowCreateSaveBinderDialog)
    }

    fun createSaveBinder(name: String, targetAmount: String, targetPeriod: String) =
        viewModelScope.launch {
            runCatching {
                _result.emit(Result.Loading)
                dataStoreRepository.setSaveBinder(
                    name = name,
                    targetAmount = targetAmount,
                    targetPeriod = targetPeriod
                )
            }.onSuccess { // 응답 성공
                _result.emit(Result.Finish)
                _action.emit(CreateBinderSaveAction.NavigateBinderSaveView)
            }.onFailure { // 응답 실패
                _result.emit(Result.Finish)
            }
        }
}

data class CreateBinderSaveState(val nothing: String = "")

sealed interface CreateBinderSaveAction {
    class ShowToast(val content: String) : CreateBinderSaveAction
    data object ShowCreateSaveBinderDialog : CreateBinderSaveAction
    data object NavigateBinderSaveView : CreateBinderSaveAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}