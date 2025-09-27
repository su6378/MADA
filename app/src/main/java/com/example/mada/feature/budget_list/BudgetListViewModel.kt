package com.example.mada.feature.budget_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.feature.account.AccountAction
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
        getSaveBinder()
        getBudgetBinderImage()
        getSaveBinderImage()
    }

    // 예산 정보 받기
    private fun getBudgetInfo() = viewModelScope.launch {
        dataStoreRepository.getBudgetExist().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _state.update { it.copy(isBudgetExist = result) }
        }
    }

    // 저축 바인더 정보 받기
    private fun getSaveBinder() = viewModelScope.launch {
        dataStoreRepository.getSaveBinder().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            if (result.isNotEmpty()) _state.update { it.copy(isSaveBinderExist = true) }
        }
    }

    // 예산 바인더 이미지 받기
    private fun getBudgetBinderImage() = viewModelScope.launch {
        dataStoreRepository.getBudgetBinderImage().onStart {
            _result.emit(Result.Loading)
        }.catch {
            _result.emit(Result.Finish)
        }.collectLatest { result ->
            _result.emit(Result.Finish)
            if (result.isEmpty()) _state.update { it.copy(budgetBinderImage = "alle") }
            else _state.update { it.copy(budgetBinderImage = result) }
        }
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

    fun showBinderImageAlert(image: String) = viewModelScope.launch {
        _action.emit(BudgetListAction.ShowSetBinderImageAlert(image))
    }

    fun setBinderImage(position: Int, image: String) = viewModelScope.launch {
        viewModelScope.launch {
            runCatching {
                _result.emit(Result.Loading)
                when(position) {
                    0 -> dataStoreRepository.setBudgetBinderImage(image)
                    1 -> dataStoreRepository.setSaveBinderImage(image)
                }
            }.onSuccess { // 응답 성공
                _result.emit(Result.Finish)
                _action.emit(BudgetListAction.SetBinderImage(image))
            }.onFailure { // 응답 실패
                _result.emit(Result.Finish)
            }
        }
    }
}

data class BudgetListState(
    var isBudgetExist: Boolean = false,
    var isSaveBinderExist: Boolean = false,
    var budgetBinderImage: String = "",
    var saveBinderImage: String = "",
)

sealed interface BudgetListAction {
    class ShowSetBinderImageAlert(val image: String) : BudgetListAction
    class SetBinderImage(val image: String) : BudgetListAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}