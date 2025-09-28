package com.example.mada.feature.binder_save

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
        getSaveBinderImage()
        getSaveBinderInfo()
        getStepInfo()
        getBudgetInfo()
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
                    startPeriod = info[2],
                    targetPeriod = "${info[3]} 만기"
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

            Log.d(TAG, "getStepInfo: $result")
            
            when (result) {
                0 -> _state.update { it.copy(saveWeek = "0회차 납임") }
                1 -> _state.update {
                    it.copy(
                        saveWeek = "1회차 납임",
                        weekList = DateUtil.getWeekInfoList(_state.value.startPeriod, 0)
                    )
                }

                2 -> _state.update {
                    it.copy(
                        saveWeek = "2회차 납임",
                        weekList = DateUtil.getWeekInfoList(_state.value.startPeriod, 1)
                    )
                }

                3 -> _state.update {
                    it.copy(
                        saveWeek = "20회차 납임",
                        weekList = DateUtil.getWeekInfoList(_state.value.startPeriod, 19)
                    )
                }

                else -> _state.update {
                    it.copy(
                        saveWeek = "21회차 납임",
                        weekList = DateUtil.getWeekInfoList(_state.value.startPeriod, 20)
                    )
                }
            }
        }
    }

    // 예산 정보 받기
    private fun getBudgetInfo() = viewModelScope.launch {
        dataStoreRepository.getBudget().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collect { result ->
            if (result.sum() > 0) {
                if (_state.value.step > 0)
                    _state.update {
                        it.copy(
                            saveMoney = (result.sum() - BudgetUtil.expenditure.sum()).toWon(),
                            budgetProgress = ((result.sum() - BudgetUtil.expenditure.sum()).toDouble() / result.sum()
                                .toDouble() * 100).roundToInt(),
                            budgetProgressText = "${
                                ((result.sum() - BudgetUtil.expenditure.sum()).toDouble() / result.sum()
                                    .toDouble() * 100).roundToInt()
                            }% 달성중",
                            saveWeekMoney = (result.sum() - BudgetUtil.expenditure.sum()).toWon(),
                        )
                    }
                else _state.update { it.copy(saveMoney = 0.toWon()) }

                Log.d(TAG, "getBudgetInfo: $result ${_state.value.step}")
                
                when (_state.value.step) {
                    1 -> _state.update {
                        it.copy(
                            saveHistoryList = listOf(
                                SaveHistory(
                                    0,
                                    (result.sum() - BudgetUtil.expenditure.sum()).toWon(),
                                    _state.value.weekList[0]
                                )
                            )
                        )
                    }

                    2 -> _state.update {
                        it.copy(
                            saveHistoryList = listOf
                                (
                                SaveHistory(0, 30000.toWon(), _state.value.weekList[0]),
                                SaveHistory(
                                    1,
                                    (result.sum() - BudgetUtil.expenditure.sum()).toWon(),
                                    _state.value.weekList[1]
                                )
                            )
                        )
                    }

                    3 -> _state.update {
                        val saveHistoryList = arrayListOf<SaveHistory>()

                        for (i in 0 until 20) {
                            saveHistoryList.add(SaveHistory(i, 150000.toWon(),_state.value.weekList[i]))
                        }

                        it.copy(saveHistoryList = saveHistoryList)
                    }
                }
            }
        }
    }
}

data class BinderSaveState(
    var saveBinderImage: String = "",
    var targetName: String = "",
    var targetAmount: String = "",
    var startPeriod: String = "",
    var targetPeriod: String = "",
    var step: Int = 0,
    var saveMoney: String = "",
    var budgetProgress: Int = 0,
    var budgetProgressText: String = "0% 달성중",
    var saveWeek: String = "",
    var saveWeekMoney: String = "",
    var saveHistoryList: List<SaveHistory> = listOf(),
    var weekList: List<String> = listOf(),
)

sealed interface BinderSaveAction {
    class ShowToast(val content: String) : BinderSaveAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}


