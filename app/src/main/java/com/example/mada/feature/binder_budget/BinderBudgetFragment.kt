package com.example.mada.feature.binder_budget

import android.content.Context
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentBinderBudgetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class BinderBudgetFragment : BaseFragment<FragmentBinderBudgetBinding, BinderBudgetViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_binder_budget
    override val viewModel: BinderBudgetViewModel by viewModels()

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        with(binding) {

        }
    }

    override fun initDataBinding() {
        binding.apply {
            vm = viewModel

            toolbarBinderList.setNavigationOnClickListener {
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
                            is BinderBudgetAction.ShowToast -> showToast(action.content)
                            is BinderBudgetAction.NavigateWeekBudgetView -> {}
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