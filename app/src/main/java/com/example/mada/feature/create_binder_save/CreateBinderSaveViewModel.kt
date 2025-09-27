package com.example.mada.feature.create_binder_save

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.R
import com.example.mada.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
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

    init {
        getSaveBinderImage()
    }

    // 저축 바인더 이미지 받기
    private fun getSaveBinderImage() = viewModelScope.launch {
        dataStoreRepository.getSaveBinderImage().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _result.emit(Result.Finish)
            if (result.isEmpty()) _state.update { it.copy(saveBinderImage = "onee") }
            else _state.update { it.copy(saveBinderImage = result) }
        }
    }

    fun setBinderImage(image: String) = viewModelScope.launch {
        viewModelScope.launch {
            runCatching {
                _result.emit(Result.Loading)
                dataStoreRepository.setSaveBinderImage(image)
            }.onSuccess { // 응답 성공
                _result.emit(Result.Finish)
                _state.update { it.copy(saveBinderImage = image) }
            }.onFailure { // 응답 실패
                _result.emit(Result.Finish)
            }
        }
    }

    fun setSaveBinderImage(image: String) = viewModelScope.launch {
        _action.emit(CreateBinderSaveAction.SetSaveBinderImage(image))
    }


    fun showCreateSaveBinderDialog() = viewModelScope.launch {
        _action.emit(CreateBinderSaveAction.ShowCreateSaveBinderDialog)
    }

    fun createSaveBinder(name: String, targetAmount: String, startPeriod: String, targetPeriod: String) =
        viewModelScope.launch {
            runCatching {
                _result.emit(Result.Loading)
                dataStoreRepository.setSaveBinder(
                    name = name,
                    targetAmount = targetAmount,
                    startPeriod =  startPeriod,
                    targetPeriod = targetPeriod
                )
            }.onSuccess { // 응답 성공
                _result.emit(Result.Finish)
                _action.emit(CreateBinderSaveAction.NavigateBinderSaveView)
            }.onFailure { // 응답 실패
                _result.emit(Result.Finish)
            }
        }

    fun showNumberPicker() = viewModelScope.launch {
        _action.emit(CreateBinderSaveAction.ShowNumberPicker)
    }

}

data class CreateBinderSaveState(
    var saveBinderImage: String = "",
)


sealed interface CreateBinderSaveAction {
    data object ShowCreateSaveBinderDialog : CreateBinderSaveAction
    data object NavigateBinderSaveView : CreateBinderSaveAction
    class SetSaveBinderImage(val image: String) : CreateBinderSaveAction
    data object ShowNumberPicker : CreateBinderSaveAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}