package com.example.mada.feature.home

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentHomeBinding
import com.example.mada.dialog.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModels()

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        with(binding) {
            showAlertDialog(dialog = AlertDialog(mainActivity) {}, viewLifecycleOwner)
        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
        }

    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collect { action ->
                        when (action) {
                            is HomeAction.ShowToast -> showToast(action.content)
                            is HomeAction.NavigateWeekBudgetView -> navigate(HomeFragmentDirections.actionHomeFragmentToWeekBudgetFragment())
                            is HomeAction.NavigateMoneyLeftView -> navigate(HomeFragmentDirections.actionHomeFragmentToMoneyLeftFragment())
                            is HomeAction.NavigateWeekSavingView -> navigate(HomeFragmentDirections.actionHomeFragmentToWeekSavingFragment())
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
                        if (state.isBudgetExist) {
                            binding.apply {
                                clHome.setBackgroundResource(R.drawable.bg_home)
                                clHome.alpha = 0.3f
                                cvHomeInitial.visibility = View.INVISIBLE
                                ivHome.visibility = View.VISIBLE
                                ivBalloon.setImageResource(R.drawable.ic_balloon)
                                tvHomeBuildComment.text = resources.getString(R.string.money_diary_build_comment)
                                tvHomeBuildComment.setTextColor(ContextCompat.getColor(requireContext(),R.color.nh_green))

                                when(state.today) {
                                    0 -> cvHomeMonday.visibility = View.VISIBLE
                                    1 -> cvHomeTuesday.visibility = View.VISIBLE
                                    2 -> cvHomeWednesday.visibility = View.VISIBLE
                                    3 -> cvHomeThursday.visibility = View.VISIBLE
                                    4 -> cvHomeFriday.visibility = View.VISIBLE
                                    5 -> cvHomeSaturday.visibility = View.VISIBLE
                                    6 -> cvHomeSunday.visibility = View.VISIBLE
                                }
                            }

                        } else {
                            binding.apply {
                                clHome.setBackgroundResource(R.color.home_initial_background)
                                clHome.alpha = 1f
                                cvHomeInitial.visibility = View.VISIBLE
                                ivHome.visibility = View.INVISIBLE
                                ivBalloon.setImageResource(R.drawable.ic_initial_balloon)
                                tvHomeBuildComment.text = resources.getString(R.string.money_diary_build_comment_initial)
                                tvHomeBuildComment.setTextColor(ContextCompat.getColor(requireContext(),R.color.home_initial_comment))
                            }
                        }
                    }
                }
            }
        }
    }
}