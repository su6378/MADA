package com.example.mada.feature.account

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentAccountBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.feature.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding, AccountViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_account
    override val viewModel: AccountViewModel by viewModels()

    override fun initView() {
        with(binding) {

        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
            toolbarAccount.setNavigationOnClickListener {
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
                            is AccountAction.ShowCreateAccountAlert -> {
                                showAlertDialog(
                                    dialog = AlertDialog(
                                        mainActivity,
                                        title = resources.getString(R.string.home_create_account),
                                        content = resources.getString(R.string.create_account_comment)
                                    ) {
                                        viewModel.createAccount()
                                    }, viewLifecycleOwner
                                )
                            }
                            is AccountAction.NavigateHomeView -> navigate(AccountFragmentDirections.actionAccountFragmentToHomeFragment())
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