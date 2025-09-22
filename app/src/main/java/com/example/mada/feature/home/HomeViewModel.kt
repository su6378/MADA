package com.example.mada.feature.home

import android.content.res.Resources
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
        getStepInfo()
        getAccountInfo()
        getBudgetExist()
        getBudgetInfo()
        getSaveBudgetExist()
    }

    // 날짜 정보 받기
    private fun getDateInfo() {
        viewModelScope.launch {
            var weekOffset = 0

            if (_state.value.step == 1) weekOffset = 1
            else if (_state.value.step == 2) weekOffset = 2
            else if (_state.value.step > 2) weekOffset = 20

            val dateInfo = DateUtil.getDateInfo(weekOffset)

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
            _state.update { it.copy(isBudgetExist = result) }
            if (!result) _state.update { it.copy(todayBudgetComment = "예산을 설정해 주세요 \uD83D\uDE03") }
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

            if (!result) _state.update { it.copy(todayBudgetComment = "계좌를 개설해 주세요 \uD83E\uDD72") }
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
                var progress = 0

                if (((((state.value.budget[state.value.today] - BudgetUtil.expenditure[state.value.today]).toDouble() / state.value.budget[state.value.today].toDouble()) * 100).roundToInt()) > 0)
                    progress =
                        ((((state.value.budget[state.value.today] - BudgetUtil.expenditure[state.value.today]).toDouble() / state.value.budget[state.value.today].toDouble()) * 100).roundToInt())

                var todayBudget = 0

                if ((result[state.value.today] - BudgetUtil.expenditure[state.value.today]) > 0) todayBudget =
                    (result[state.value.today] - BudgetUtil.expenditure[state.value.today])

                _state.update {
                    it.copy(
                        todayProgress = progress,
                        todayProgressText = "${progress}%",
                        todayBudgetComment = "${todayBudget.toWon()} 남았어요!",
                        todayBudget = result[state.value.today].toWon(),
                        weekLeftContent = "${(result.sum() - BudgetUtil.expenditure.sum()).toWon()} 저축할 수 있어요!"
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        todayProgress = 0,
                        todayProgressText = "0%",
                        todayBudget = 0.toWon(),
                        weekLeftContent = "${0.toWon()} 저축할 수 있어요!"
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
            getDateInfo()
        }
    }

    private fun getSaveBudgetExist() = viewModelScope.launch {
        dataStoreRepository.getSaveBinder().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            if (result.isNotEmpty()) _state.update { it.copy(isSaveBudgetExist = true) }
        }
    }

    fun navigateWeekSavingFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateWeekSavingView)
    }

    fun navigateBinderListFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateBinderListView)
    }

    fun navigateBinderBudgetFragment() = viewModelScope.launch {
        _action.emit(HomeAction.ShowCreateBudgetDialog)
    }

    fun shareHomeImage() = viewModelScope.launch {
        _action.emit(HomeAction.ShareHomeImage)
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
    var todayBudgetComment: String = "0원 남았어요!",
    var weekLeftContent: String = "0원 저축할 수 있어요!",
    var isSaveAble: Boolean = false,
    var step: Int = 0,
    var isSaveBudgetExist: Boolean = false,
)

sealed interface HomeAction {
    class ShowToast(val content: String) : HomeAction
    data object NavigateWeekSavingView : HomeAction
    data object NavigateBinderListView : HomeAction
    data object ShowCreateBudgetDialog : HomeAction
    data object ShareHomeImage : HomeAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}