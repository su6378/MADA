package com.example.mada.feature.account_link

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentAccountLinkBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.feature.account.AccountFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountLinkFragment : BaseFragment<FragmentAccountLinkBinding, AccountLinkViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_account_link
    override val viewModel: AccountLinkViewModel by viewModels()

    override fun initView() {
        with(binding) {

        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
            toolbarAccountLink.setNavigationOnClickListener {
                backNavigate()
            }
        }

    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collect { action ->
                        when (action) {
                            is AccountLinkAction.ShowCreateAccountLinkAlert -> {
                                showAlertDialog(
                                    dialog = AlertDialog(
                                        mainActivity,
                                        title = resources.getString(R.string.account_link_account),
                                        content = resources.getString(R.string.account_link_account_comment)
                                    ) {
                                        viewModel.createAccountLink()
                                    }, viewLifecycleOwner
                                )
                            }
                            is AccountLinkAction.NavigateAccountView -> navigate(
                                AccountLinkFragmentDirections.actionAccountLinkFragmentToAccountFragment()
                            )
                            is AccountLinkAction.NavigateHomeView -> navigate(
                                AccountLinkFragmentDirections.actionAccountLinkFragmentToHomeFragment())
                        }
                    }
                }

                launch {
                    viewModel.result.collect { result ->
                        when (result) {
                            Result.Finish -> {
                            }

                            Result.Loading -> {
                            }

                            Result.Process -> {
                            }
                        }
                    }
                }

                launch {
                    viewModel.state.collect { state ->


                    }
                }
            }
        }
    }
}