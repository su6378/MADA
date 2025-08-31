package com.example.mada.feature.on_boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class OnBoardingViewModel @Inject constructor(
) : ViewModel() {
    private val _action: MutableSharedFlow<OnBoardingAction> = MutableSharedFlow()
    val action: SharedFlow<OnBoardingAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<OnBoardingState> = MutableStateFlow(OnBoardingState())
    val state: StateFlow<OnBoardingState> get() = _state.asStateFlow()

    // 요청
    fun request() {
        viewModelScope.launch {
            _result.emit(Result.Loading)

            runCatching {

            }.onSuccess { // 응답 성공

            }.onFailure { // 응답 실패

            }
        }
    }

    fun navigateHomeFragment() = viewModelScope.launch {
        _action.emit(OnBoardingAction.NavigateHomeView)
    }
}

data class OnBoardingState(
    val dataSomething: String = "",
)

sealed interface OnBoardingAction {
    class ShowToast(val content: String) : OnBoardingAction
    data object NavigateHomeView : OnBoardingAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}