package com.example.mada.feature.week_saving

import android.util.Log
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DX"

@HiltViewModel
class WeekSavingViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<WeekSavingAction> = MutableSharedFlow()
    val action: SharedFlow<WeekSavingAction> get() = _action.asSharedFlow()

    private val _result: MutableStateFlow<Result> = MutableStateFlow(Result.Loading)
    val result: StateFlow<Result> get() = _result.asStateFlow()

    private val _state: MutableStateFlow<WeekSavingState> = MutableStateFlow(WeekSavingState())
    val state: StateFlow<WeekSavingState> get() = _state.asStateFlow()

    private val _budget: MutableStateFlow<List<Int>> = MutableStateFlow(arrayListOf())
    val budget: StateFlow<List<Int>> get() = _budget.asStateFlow()

    init {
        getBudget()
    }

    //예산 요청
    private fun getBudget() {
        viewModelScope.launch {
            dataStoreRepository.getBudget().onStart {
                _result.emit(Result.Loading)
            }.catch {
                _result.emit(Result.Finish)
            }.collectLatest { result ->
                _result.emit(Result.Finish)
                _budget.emit(result)
            }
        }
    }

    fun navigateWeekBudgetFragment() = viewModelScope.launch {
        _action.emit(WeekSavingAction.NavigateWeekBudgetView)
    }
}

data class WeekSavingState(
    val dataSomething: String = "",
)

sealed interface WeekSavingAction {
    class ShowToast(val content: String) : WeekSavingAction
    data object NavigateWeekBudgetView : WeekSavingAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}