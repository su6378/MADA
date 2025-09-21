package com.example.mada.feature.binder_save

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
import kotlin.math.roundToInt

private const val TAG = "DX"

@HiltViewModel
class BinderSaveViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<BinderSaveAction> = MutableSharedFlow()
    val action: SharedFlow<BinderSaveAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<BinderSaveState> = MutableStateFlow(BinderSaveState())
    val state: StateFlow<BinderSaveState> get() = _state.asStateFlow()

    init {
        getSaveBinderInfo()
        getStepInfo()
        getBudgetInfo()
    }

    private fun getSaveBinderInfo() = viewModelScope.launch {
        dataStoreRepository.getSaveBinder().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            val info = result.toList()

            _state.update {
                it.copy(
                    targetName = info[0],
                    targetAmount = info[1],
                    targetPeriod = info[2]
                )
            }
        }
    }

    private fun getStepInfo() = viewModelScope.launch {
        dataStoreRepository.getStep().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _state.update {
                it.copy(step = result)
            }
        }
    }

    // 예산 정보 받기
    private fun getBudgetInfo() = viewModelScope.launch {
        dataStoreRepository.getBudget().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            if (result.sum() > 0) {
                if (_state.value.step > 1)
                    _state.update {
                        it.copy(
                            saveMoney = (result.sum() - BudgetUtil.expenditure.sum()).toWon(),
                            budgetProgress = ((result.sum() - BudgetUtil.expenditure.sum()).toDouble() / result.sum().toDouble() * 100).roundToInt(),
                            budgetProgressText ="${((result.sum() - BudgetUtil.expenditure.sum()).toDouble() / result.sum().toDouble() * 100).roundToInt()}% 달성중",
                            saveWeek = DateUtil.getCurrentWeekInfo(),
                            saveWeekMoney = (result.sum() - BudgetUtil.expenditure.sum()).toWon()
                        )
                    }
                else _state.update { it.copy(saveMoney = 0.toWon()) }
            } else {
                _state.update { it.copy(saveMoney = 0.toWon()) }
            }
        }
    }
}

data class BinderSaveState(
    var targetName: String = "",
    var targetAmount: String = "",
    var targetPeriod: String = "",
    var step: Int = 0,
    var saveMoney: String = "",
    var budgetProgress: Int = 0,
    var budgetProgressText: String = "0% 달성중",
    var saveWeek: String = "아직 저축을 하지 않았어요!",
    var saveWeekMoney: String = "",
)

sealed interface BinderSaveAction {
    class ShowToast(val content: String) : BinderSaveAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}