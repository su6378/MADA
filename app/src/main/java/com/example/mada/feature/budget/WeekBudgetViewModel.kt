package com.example.mada.feature.budget

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
class WeekBudgetViewModel @Inject constructor(
) : ViewModel() {
    private val _action: MutableSharedFlow<WeekBudgetAction> = MutableSharedFlow()
    val action: SharedFlow<WeekBudgetAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<WeekBudgetState> = MutableStateFlow(WeekBudgetState())
    val state: StateFlow<WeekBudgetState> get() = _state.asStateFlow()

    // 질문 요청
    fun request() {
        viewModelScope.launch {
            _result.emit(Result.Loading)

            runCatching {

            }.onSuccess { // 응답 성공

            }.onFailure { // 응답 실패

            }
        }
    }
}

data class WeekBudgetState(
    val dataSomething: String = "",
)

sealed interface WeekBudgetAction {
    class ShowToast(val content: String) : WeekBudgetAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}