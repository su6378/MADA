package com.example.mada.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mada.repository.DataStoreRepository
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
class CardViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<CardAction> = MutableSharedFlow()
    val action: SharedFlow<CardAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<CardState> = MutableStateFlow(CardState())
    val state: StateFlow<CardState> get() = _state.asStateFlow()

    // 요청
    private fun createCard() {
        viewModelScope.launch {
            runCatching {
                _result.emit(Result.Loading)
                dataStoreRepository.setCard(true)
            }.onSuccess { // 응답 성공
                _result.emit(Result.Finish)
                _action.emit(CardAction.NavigateHomeView)
            }.onFailure { // 응답 실패
                _result.emit(Result.Finish)
            }
        }
    }

    fun navigateHomeFragment() = viewModelScope.launch {
        createCard()
    }
}

data class CardState(
    val dataSomething: String = "",
)

sealed interface CardAction {
    class ShowToast(val content: String) : CardAction
    data object NavigateHomeView : CardAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}