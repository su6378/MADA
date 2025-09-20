package com.example.mada.feature.card

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentCardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardFragment : BaseFragment<FragmentCardBinding, CardViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_card
    override val viewModel: CardViewModel by viewModels()

    override fun initView() {
        with(binding) {

        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
            toolbarCard.setNavigationOnClickListener {
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
                            is CardAction.ShowToast -> showToast(action.content)
                            is CardAction.NavigateHomeView -> navigate(CardFragmentDirections.actionCardFragmentToHomeFragment())
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