package com.example.mada.feature.binder_budget

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.repository.DataStoreRepository
import com.example.mada.util.BudgetUtil
import com.example.mada.util.DateUtil
import com.example.mada.util.TextUtil.toWon
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
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt

private const val TAG = "DX"

@HiltViewModel
class BinderBudgetViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<BinderBudgetAction> = MutableSharedFlow()
    val action: SharedFlow<BinderBudgetAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<BinderBudgetState> = MutableStateFlow(BinderBudgetState())
    val state: StateFlow<BinderBudgetState> get() = _state.asStateFlow()

    private val days = listOf("월요일", "화요알", "수요일", "목요알", "금요일", "토요일", "일요일")

    init {
        getDateInfo()
    }

    // 날짜 정보 받기
    private fun getDateInfo() {
        viewModelScope.launch {
            val today = DateUtil.getToday()
            val dateInfo = DateUtil.getDateInfo()

            Log.d(TAG, "getDateInfo: ${DateUtil.getDateInfo()}")
            _state.update {
                it.copy(
                    day = days[today],
                    today = today,
                    todayText = "${dateInfo.first()} ${dateInfo[today + 1]}일"
                )
            }

            getBudgetInfo()
        }
    }

    // 예산 정보 받기
    private fun getBudgetInfo() = viewModelScope.launch {
        dataStoreRepository.getBudget().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _state.update {
                it.copy(
                    budgetList = result,
                    leftBudget = (result[_state.value.today] - BudgetUtil.expenditure[_state.value.today]).toWon(),
                    budget = result[_state.value.today].toWon(),
                    expenditure = BudgetUtil.expenditure[_state.value.today].toWon(),
                    budgetProgress = ((result[_state.value.today] - BudgetUtil.expenditure[_state.value.today]).toDouble() / result[_state.value.today].toDouble() * 100).roundToInt(),
                    budgetProgressText = "${((result[_state.value.today] - BudgetUtil.expenditure[_state.value.today]).toDouble() / result[_state.value.today].toDouble() * 100).roundToInt()}%"
                )
            }
        }
    }
}

data class BinderBudgetState(
    var today: Int = 0,
    var todayText: String = "",
    var day: String = "",
    var budgetList: List<Int> = arrayListOf(),
    var leftBudget: String = "",
    var budget: String = "",
    var expenditure: String = "",
    var budgetProgress: Int = 0,
    var budgetProgressText: String = ""
)

sealed interface BinderBudgetAction {
    class ShowToast(val content: String) : BinderBudgetAction
    data object NavigateWeekBudgetView : BinderBudgetAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}