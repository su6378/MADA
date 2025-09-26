package com.example.mada.feature.binder_save

import android.content.Context
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentBinderSaveBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class BinderSaveFragment : BaseFragment<FragmentBinderSaveBinding, BinderSaveViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_binder_save
    override val viewModel: BinderSaveViewModel by viewModels()

    override fun initView() {
        with(binding) {

        }
    }

    override fun initDataBinding() {
        binding.apply {
            vm = viewModel

            toolbarBinderSave.setNavigationOnClickListener {
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
                            is BinderSaveAction.ShowToast -> showToast(action.content)
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