package com.example.mada.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.repository.DataStoreRepository
import com.example.mada.util.DateUtil
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
        getBudgetInfo()
    }

    // 날짜 정보 받기
    private fun getDateInfo() {
        viewModelScope.launch {
            val dateInfo = DateUtil.getDateInfo()
            val today = DateUtil.getToday()

            _state.update {
                it.copy(
                    month = dateInfo.first(),
                    week = dateInfo.subList(1, dateInfo.lastIndex + 1),
                    today = today
                )
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
                _state.update {
                    it.copy(
                        isBudgetExist = true
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isBudgetExist = false
                    )
                }
            }
        }
    }

    fun navigateWeekBudgetFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateWeekBudgetView)
    }

    fun navigateMoneyLeftFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateMoneyLeftView)
    }

    fun navigateWeekSavingFragment() = viewModelScope.launch {
        _action.emit(HomeAction.NavigateWeekSavingView)
    }
}

data class HomeState(
    var isBudgetExist: Boolean = false,
    var month: String = "",
    val week: List<String> = arrayListOf(),
    var today: Int = 0,
    var todayLeftContent: String = "예산을 설정해 주세요 \uD83D\uDE03",
    var todayProgress: String = "0%",
    var todayBudget: String = "0원",
    var weekLeftContent: String = "0원 저축할 수 있어요!"
)

sealed interface HomeAction {
    class ShowToast(val content: String) : HomeAction
    data object NavigateWeekBudgetView : HomeAction
    data object NavigateMoneyLeftView : HomeAction
    data object NavigateWeekSavingView : HomeAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}