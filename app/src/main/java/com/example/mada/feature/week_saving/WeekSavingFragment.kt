package com.example.mada.feature.week_saving

import android.content.Context
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentWeekSavingBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.feature.week_budget.WeekBudgetFragmentDirections
import com.example.mada.util.BudgetUtil
import com.example.mada.util.TextUtil.setColoredSubstrings
import com.example.mada.util.TextUtil.toWon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class WeekSavingFragment : BaseFragment<FragmentWeekSavingBinding, WeekSavingViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_week_saving
    override val viewModel: WeekSavingViewModel by viewModels()

    override fun initView() {
        with(binding) {}
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
            toolbarWeekSaving.setNavigationOnClickListener {
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
                            WeekSavingAction.ShowSetBudgetDialog -> {
                                showAlertDialog(
                                    dialog = AlertDialog(
                                        mainActivity,
                                        title = resources.getString(R.string.money_left_save),
                                        content = resources.getString(R.string.money_left_save_comment)
                                    ) {
                                        viewModel.setThisWeekBudget()
                                    }, viewLifecycleOwner
                                )
                            }
                            WeekSavingAction.NavigateHomeView -> {
                                showToast("이번주 예산을 저축했습니다 \uD83D\uDE03")
                                backNavigate()
                            }
                        }
                    }
                }

                launch {
                    viewModel.result.collect { result ->
                        when (result) {
                            Result.Finish -> {}

                            Result.Loading -> {}

                            Result.Process -> {}
                        }
                    }
                }

                launch {
                    viewModel.state.collect { state ->


                    }
                }


                launch {
                    viewModel.budget.collect { budget ->
                        binding.apply {
                            val budgetValue = vm!!.budget.value
                            val comment = "이번주 예산 " + budgetValue.sum()
                                .toWon() + " 중에 " + (budgetValue.sum() - BudgetUtil.expenditure.sum()).toWon()+ "이 남았어요!"
                            val ranges = listOf(budgetValue.sum().toWon(), (budgetValue.sum() - BudgetUtil.expenditure.sum()).toWon())

                            tvWeekSavingComment.setColoredSubstrings(comment, ranges, colorRes = R.color.nh_green)

                            setWeekSavingMoney(budgetValue, BudgetUtil.expenditure)

                            setWeekSavingMoneyImage(budgetValue, BudgetUtil.expenditure)


                        }
                    }
                }
            }
        }
    }

    private fun setWeekSavingMoney(budget: List<Int>, expenditure: IntArray) {
        binding.apply {
            tvWeekSavingMondayMoney.text = (budget[0] - expenditure[0]).toWon()
            tvWeekSavingTuesdayMoney.text = (budget[1] - expenditure[1]).toWon()
            tvWeekSavingWednesdayMoney.text = (budget[2] - expenditure[2]).toWon()
            tvWeekSavingThursdayMoney.text = (budget[3] - expenditure[3]).toWon()
            tvWeekSavingFridayMoney.text = (budget[4] - expenditure[4]).toWon()
            tvWeekSavingSaturdayMoney.text = (budget[5] - expenditure[5]).toWon()
            tvWeekSavingSundayMoney.text = (budget[6] - expenditure[6]).toWon()
            tvWeekSavingWeekMoney.text = (budget.sum() - expenditure.sum()).toWon()
        }
    }

    // 요일별 저축 가능한 금액에 따라 이미지 유무 세팅
    private fun setWeekSavingMoneyImage(budget: List<Int>, expenditure: IntArray) {
        binding.apply {
            if (budget[0] - expenditure[0] > 0) ivWeekSavingMonday.setImageResource(R.drawable.ic_small_money)
            if (budget[1] - expenditure[1] > 0) ivWeekSavingTuesday.setImageResource(R.drawable.ic_small_money)
            if (budget[2] - expenditure[2] > 0) ivWeekSavingWednesday.setImageResource(R.drawable.ic_small_money)
            if (budget[3] - expenditure[3] > 0) ivWeekSavingThursday.setImageResource(R.drawable.ic_small_money)
            if (budget[4] - expenditure[4] > 0) ivWeekSavingFriday.setImageResource(R.drawable.ic_small_money)
            if (budget[5] - expenditure[5] > 0) ivWeekSavingSaturday.setImageResource(R.drawable.ic_small_money)
            if (budget[6] - expenditure[6] > 0) ivWeekSavingSunday.setImageResource(R.drawable.ic_small_money)
            if (budget.sum() - expenditure.sum() > 0) {
                ivWeekSavingWeek.setImageResource(R.drawable.ic_big_money)
                ivBlinker.setImageResource(R.drawable.ic_blinker_green)
            }

        }
    }
}