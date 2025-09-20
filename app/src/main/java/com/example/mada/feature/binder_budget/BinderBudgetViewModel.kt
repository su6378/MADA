package com.example.mada.feature.binder_budget

import androidx.lifecycle.ViewModel
import com.example.mada.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

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

    }
}

data class BinderBudgetState(
    var isSaveAble: Boolean = false,
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