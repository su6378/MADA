package com.example.mada.feature.allone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.repository.DataStoreRepository
import com.example.mada.util.BudgetUtil
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

    private val _account: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val account: StateFlow<Boolean> get() = _account.asStateFlow()

    private val _money: MutableStateFlow<Int> = MutableStateFlow(0)
    val money: StateFlow<Int> get() = _money.asStateFlow()

    init {
        getAccount()
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
                _account.emit(result)
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

                _money.emit(result[today] - BudgetUtil.expenditure[today])
            }
        }
    }

    fun navigateOnBoardingFragment() = viewModelScope.launch {
        _action.emit(AllOneAction.NavigateOnBoardingView)
    }
}

data class AllOneState(
    val dataSomething: String = "",
)

sealed interface AllOneAction {
    class ShowToast(val content: String) : AllOneAction
    data object NavigateOnBoardingView : AllOneAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}