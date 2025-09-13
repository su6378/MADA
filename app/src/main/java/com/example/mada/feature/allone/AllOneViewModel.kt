package com.example.mada.feature.allone

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

private const val TAG = "DX"

@HiltViewModel
class AllOneViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<AllOneAction> = MutableSharedFlow()
    val action: SharedFlow<AllOneAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<AllOneState> = MutableStateFlow(AllOneState())
    val state: StateFlow<AllOneState> get() = _state.asStateFlow()

    init {
        getAccount()
        getCard()
        getMoney()
    }

    // 계좌 요청
    private fun getAccount() {
        viewModelScope.launch {
            dataStoreRepository.getAccount().onStart {
                _result.emit(Result.Loading)
            }.catch {
                _result.emit(Result.Finish)
            }.collectLatest { result ->
                if (result) _state.update { it.copy(isSigned = true) }
            }
        }
    }

    private fun getMoney() {
        viewModelScope.launch {
            dataStoreRepository.getBudget().onStart {
                _result.emit(Result.Loading)
            }.catch {
                _result.emit(Result.Finish)
            }.collectLatest { result ->
                val today = DateUtil.getToday()

                if (result.sum() > 0) _state.update {
                    it.copy(
                        isBudgetExist = true,
                        money = result[today] - BudgetUtil.expenditure[today]
                    )
                }
            }
        }
    }

    private fun getCard() {
        viewModelScope.launch {
            dataStoreRepository.getCard().onStart {
                _result.emit(Result.Loading)
            }.catch {
                _result.emit(Result.Finish)
            }.collectLatest { result ->
                _state.update { it.copy(isCreated = result) }
            }
        }
    }

    fun navigateNextFragment() = viewModelScope.launch {
        if (_state.value.isCreated) _action.emit(AllOneAction.NavigateHomeView)
        else {
            if (_state.value.isSigned) _action.emit(AllOneAction.NavigationCardView)
            else _action.emit(AllOneAction.NavigateOnBoardingView)
        }
    }
}

data class AllOneState(
    var isSigned: Boolean = false, // 계좌 개설 여부
    var isCreated: Boolean = false, // 카드 개설 여부
    var isBudgetExist: Boolean = false, // 예산 설정 여부
    var money: Int = 0 // 오늘 쓸 수 있는 돈
)

sealed interface AllOneAction {
    class ShowToast(val content: String) : AllOneAction
    data object NavigateOnBoardingView : AllOneAction
    data object NavigationCardView : AllOneAction
    data object NavigateHomeView : AllOneAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}