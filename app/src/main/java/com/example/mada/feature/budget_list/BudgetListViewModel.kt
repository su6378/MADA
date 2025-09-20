package com.example.mada.feature.budget_list

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DX"

@HiltViewModel
class BudgetListViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<BudgetListAction> = MutableSharedFlow()
    val action: SharedFlow<BudgetListAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<BudgetListState> = MutableStateFlow(BudgetListState())
    val state: StateFlow<BudgetListState> get() = _state.asStateFlow()

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
            if (result.sum() > 0) {
                _state.update { it.copy(isBudgetExist = true) }
            } else {
                _state.update { it.copy(isBudgetExist = false) }
            }
        }
    }
}

data class BudgetListState(
    var isBudgetExist: Boolean = false,
)

sealed interface BudgetListAction {
    class ShowToast(val content: String) : BudgetListAction
    data object ShowBudgetDialog : BudgetListAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}