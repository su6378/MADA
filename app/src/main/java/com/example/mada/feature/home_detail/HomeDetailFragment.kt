package com.example.mada.feature.home_detail

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentHomeDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeDetailFragment : BaseFragment<FragmentHomeDetailBinding, HomeDetailViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_home_detail
    override val viewModel: HomeDetailViewModel by viewModels()

    override fun initView() {
        with(binding) {
        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
            toolbarHomeDetail.setNavigationOnClickListener {
                backNavigate()
            }
        }

    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collect { action ->
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