package com.example.mada.feature.binder_budget

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.repository.DataStoreRepository
import com.example.mada.util.BudgetUtil
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

    init {
        getBudgetInfo()
    }

    // 예산 정보 받기
    private fun getBudgetInfo() = viewModelScope.launch {
        dataStoreRepository.getBudget().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            Log.d(TAG, "getBudgetInfo: ${result[0]} ${BudgetUtil.expenditure[0]}\n")
            Log.d(
                TAG,
                "getBudgetInfo: ${round((result[0] - BudgetUtil.expenditure[0]).toDouble() / result[0].toDouble())}"
            )
            _state.update {
                it.copy(
                    budgetList = result,
                    leftBudget = (result[0] - BudgetUtil.expenditure[0]).toWon(),
                    budget = result[0].toWon(),
                    expenditure = BudgetUtil.expenditure[0].toWon(),
                    budgetProgress = ((result[0] - BudgetUtil.expenditure[0]).toDouble() / result[0].toDouble() * 100).roundToInt(),
                    budgetProgressText = "${((result[0] - BudgetUtil.expenditure[0]).toDouble() / result[0].toDouble() * 100).roundToInt()}%"
                )
            }
        }
    }
}

data class BinderBudgetState(
    var isSaveAble: Boolean = false,
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