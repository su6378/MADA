package com.example.mada.feature.on_boarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    fun navigateAccountFragment() = viewModelScope.launch {
        _state.update {
            it.copy(step = it.step.plus(1))
        }

        if (_state.value.step >= 9) _action.emit(OnBoardingAction.NavigateAccountLinkView)
    }

    fun navigateHomeFragment() = viewModelScope.launch {
        _action.emit(OnBoardingAction.NavigateHomeView)
    }
}

data class OnBoardingState(
    var guideComment: String = "",
    var step: Int = 1,
)

sealed interface OnBoardingAction {
    class ShowToast(val content: String) : OnBoardingAction
    data object NavigateHomeView : OnBoardingAction
    data object NavigateAccountLinkView : OnBoardingAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}