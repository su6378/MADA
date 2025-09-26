package com.example.mada.feature.account_link

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
class AccountLinkViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _action: MutableSharedFlow<AccountLinkAction> = MutableSharedFlow()
    val action: SharedFlow<AccountLinkAction> get() = _action.asSharedFlow()

    private val _result: MutableSharedFlow<Result> = MutableSharedFlow()
    val result: SharedFlow<Result> get() = _result.asSharedFlow()

    private val _state: MutableStateFlow<AccountLinkState> = MutableStateFlow(AccountLinkState())
    val state: StateFlow<AccountLinkState> get() = _state.asStateFlow()

    fun createAccountLink() {
        viewModelScope.launch {
            runCatching {
                _result.emit(Result.Loading)
                dataStoreRepository.setAccount(true)
            }.onSuccess { // 응답 성공
                _result.emit(Result.Finish)
                _action.emit(AccountLinkAction.NavigateHomeView)
            }.onFailure { // 응답 실패
                _result.emit(Result.Finish)
            }
        }
    }

    fun navigateAccountFragment() = viewModelScope.launch {
        _action.emit(AccountLinkAction.NavigateAccountView)
    }

    fun showCreateAccountLinkAlert() = viewModelScope.launch {
        _action.emit(AccountLinkAction.ShowCreateAccountLinkAlert)
    }
}

data class AccountLinkState(
    val dataSomething: String = "",
)

sealed interface AccountLinkAction {
    data object ShowCreateAccountLinkAlert: AccountLinkAction
    data object NavigateAccountView : AccountLinkAction
    data object NavigateHomeView : AccountLinkAction
}

sealed interface Result {
    data object Loading : Result
    data object Process : Result
    data object Finish : Result
}