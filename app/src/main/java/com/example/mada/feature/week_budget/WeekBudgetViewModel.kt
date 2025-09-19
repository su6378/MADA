package com.example.mada.feature.week_budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.repository.DataStoreRepository
import com.example.mada.util.BudgetUtil
import com.example.mada.util.DateUtil
import com.example.mada.util.TextUtil.toWon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
class WeekBudgetViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<WeekBudgetAction> = MutableSharedFlow()
    val action: SharedFlow<WeekBudgetAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<WeekBudgetState> = MutableStateFlow(WeekBudgetState())
    val state: StateFlow<WeekBudgetState> get() = _state.asStateFlow()

    init {
        getDateInfo()
        getMyData()
    }

    // 날짜 정보 받기
    private fun getDateInfo() {
        viewModelScope.launch {
            val dateInfo = DateUtil.getDateInfo()
            val today = DateUtil.getToday()
            _state.update {
                it.copy(
                    weekInfo = dateInfo.last(),
                    dayInfo = "${dateInfo[0][0]}.${dateInfo[1]}(월) ~ ${dateInfo[0][0]}.${dateInfo[7]}(일)"
                )
            }
        }
    }

    // 마이데이터 연동
    private fun getMyData() {
        viewModelScope.launch {
            val budgetData = BudgetUtil.getRandomMoney().toList()
            delay(2000)
            _state.update {
                it.copy(
                    isMyDataLinked = true,
                    budget = budgetData,
                    sumBudget = "총 ${budgetData.sum().toWon()}"
                )
            }
        }
    }

    // 자산 설정
    fun setBudgetInfo(budgetInfo: IntArray) {
        viewModelScope.launch {
            runCatching {
                _result.emit(Result.Loading)
                dataStoreRepository.setBudget(
                    monday = budgetInfo[0],
                    tuesday = budgetInfo[1],
                    wednesday = budgetInfo[2],
                    thursday = budgetInfo[3],
                    friday = budgetInfo[4],
                    saturday = budgetInfo[5],
                    sunday = budgetInfo[6]
                )
            }.onSuccess { // 응답 성공
                _result.emit(Result.Finish)
                _action.emit(WeekBudgetAction.NavigateHomeView)
            }.onFailure { // 응답 실패
                _result.emit(Result.Finish)
            }
        }
    }

    fun navigateHomeFragment() = viewModelScope.launch {
        _action.emit(WeekBudgetAction.ShowBudgetDialog)
    }
}

data class WeekBudgetState(
    var weekInfo: String = "",
    var dayInfo: String = "",
    var isMyDataLinked: Boolean = false,
    var budget: List<Int> = arrayListOf(),
    var sumBudget: String = "",
)

sealed interface WeekBudgetAction {
    class ShowToast(val content: String) : WeekBudgetAction
    data object ShowBudgetDialog : WeekBudgetAction
    data object NavigateHomeView : WeekBudgetAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}