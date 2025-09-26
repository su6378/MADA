package com.example.mada.feature.card

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
    fun createCard() {
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
        _action.emit(CardAction.ShowCreateCardAlert)
    }
}

data class CardState(
    val dataSomething: String = "",
)

sealed interface CardAction {
    data object ShowCreateCardAlert: CardAction
    data object NavigateHomeView : CardAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}