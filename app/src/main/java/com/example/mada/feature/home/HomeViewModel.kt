package com.example.mada.feature.home

import android.content.res.Resources
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
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<HomeAction> = MutableSharedFlow()
    val action: SharedFlow<HomeAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> get() = _state.asStateFlow()

    init {
        getDateInfo()
        getAccountInfo()
        getBudgetExist()
        getBudgetInfo()
        getStepInfo()
    }

    // 날짜 정보 받기
    private fun getDateInfo() {
        viewModelScope.launch {
            val dateInfo = DateUtil.getDateInfo()
//            val today = DateUtil.getToday()
            val today = 6

            _state.update {
                it.copy(
                    month = dateInfo.first(),
                    week = dateInfo.subList(1, dateInfo.lastIndex),
                    today = today
                )
            }

            if (today == 6) {
                _state.update {
                    it.copy(isSaveAble = true)
                }
            }
        }
    }

    // 예산 설정 유무
    private fun getBudgetExist() = viewModelScope.launch {
        dataStoreRepository.getBudgetExist().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _state.update {
                it.copy(isBudgetExist = result)
            }
        }
    }

    // 계좌 개설 유무
    private fun getAccountInfo() = viewModelScope.launch {
        dataStoreRepository.getAccount().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _state.update { it.copy(isSigned = result) }
        }
    }

    // 예산 정보 받기
    private fun getBudgetInfo() = viewModelScope.launch {
        dataStoreRepository.getBudget().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _state.update { it.copy(budget = result) }

            if (result.isNotEmpty() && _state.value.isBudgetExist) {
                val progress =
                    (((state.value.budget[state.value.today] - BudgetUtil.expenditure[state.value.today]).toDouble() / state.value.budget[state.value.today].toDouble()) * 100).roundToInt()

                _state.update {
                    it.copy(
                        todayProgress = progress,
                        todayProgressText = "${progress}%",
                        todayBudget = result[state.value.today].toWon(),
                        weekLeftContent = "${(result[state.value.today] - BudgetUtil.expenditure[state.value.today]).toWon()} 저축할 수 있어요!"
                    )
                }
            }
        }
    }

    private fun getStepInfo() = viewModelScope.launch {
        dataStoreRepository.getStep().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _state.update { it.copy(step = result) }
        }
    }

    fun navigateWeekBudgetFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateWeekBudgetView)
    }

    fun navigateWeekSavingFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateWeekSavingView)
    }

    fun navigateAccountFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateOnBoardingView)
    }

    fun navigateBinderListFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateBinderListView)
    }
}

data class HomeState(
    var isSigned: Boolean = false,
    var isBudgetExist: Boolean = false,
    var budget: List<Int> = arrayListOf(),
    var month: String = "",
    val week: List<String> = arrayListOf(),
    var today: Int = 0,
    var todayProgress: Int = 0,
    var todayProgressText: String = "0%",
    var todayBudget: String = "0원",
    var weekLeftContent: String = "0원 저축할 수 있어요!",
    var isSaveAble: Boolean = false,
    var step: Int = 0,
)

sealed interface HomeAction {
    class ShowToast(val content: String) : HomeAction
    data object NavigateWeekBudgetView : HomeAction
    data object NavigateWeekSavingView : HomeAction
    data object NavigateOnBoardingView : HomeAction
    data object NavigateBinderListView : HomeAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}