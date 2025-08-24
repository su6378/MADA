package com.example.mada.feature.budget

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentWeekBudgetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class WeekBudgetFragment : BaseFragment<FragmentWeekBudgetBinding, WeekBudgetViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_week_budget
    override val viewModel: WeekBudgetViewModel by viewModels()

    override fun initView() {
        with(binding) {

        }
    }

    override fun initDataBinding() {
        binding.apply {
            vm = viewModel

            toolbarWeekBudget.setNavigationOnClickListener {
                backNavigate()
            }

            toggleCalendar.setOnCheckedChangeListener { toggle, isChecked -> // 캘린더 연동

                if (isChecked) {
                    tvWeekBudgetComment.text = resources.getString(R.string.week_budget_comment2)
                    tvWeekBudgetComment.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
                    tvWeekBudgetFridayComment.visibility = View.VISIBLE
                    cvWeekBudgetFriday.visibility = View.VISIBLE
                    tvWeekBudgetFridayBudget.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
                    tvWeekBudgetSaturdayComment.visibility = View.VISIBLE
                    cvWeekBudgetSaturday.visibility = View.VISIBLE
                    tvWeekBudgetSaturdayBudget.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
                    tvWeekBudgetSundayComment.visibility = View.VISIBLE
                    cvWeekBudgetSunday.visibility = View.VISIBLE
                    tvWeekBudgetSundayBudget.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))

                }else {
                    tvWeekBudgetComment.text = resources.getString(R.string.week_budget_comment)
                    tvWeekBudgetComment.setTextColor(ContextCompat.getColor(requireContext(),R.color.nh_green))
                    tvWeekBudgetFridayComment.visibility = View.GONE
                    tvWeekBudgetFridayBudget.setTextColor(ContextCompat.getColor(requireContext(),R.color.nh_green))
                    cvWeekBudgetFriday.visibility = View.INVISIBLE
                    tvWeekBudgetSaturdayComment.visibility = View.GONE
                    cvWeekBudgetSaturday.visibility = View.INVISIBLE
                    tvWeekBudgetSaturdayBudget.setTextColor(ContextCompat.getColor(requireContext(),R.color.nh_green))
                    tvWeekBudgetSundayComment.visibility = View.GONE
                    cvWeekBudgetSunday.visibility = View.INVISIBLE
                    tvWeekBudgetSundayBudget.setTextColor(ContextCompat.getColor(requireContext(),R.color.nh_green))
                }

            }
        }
    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collect { action ->
                        when (action) {
                            is WeekBudgetAction.ShowToast -> showToast(action.content)
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