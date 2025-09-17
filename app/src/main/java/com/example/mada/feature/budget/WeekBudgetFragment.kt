package com.example.mada.feature.budget

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentWeekBudgetBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.dialog.LoadingDialog
import com.example.mada.feature.home.HomeFragmentDirections
import com.example.mada.util.TextUtil
import com.example.mada.util.TextUtil.toWon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class WeekBudgetFragment : BaseFragment<FragmentWeekBudgetBinding, WeekBudgetViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_week_budget
    override val viewModel: WeekBudgetViewModel by viewModels()

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

            toolbarWeekBudget.setNavigationOnClickListener {
                backNavigate()
            }

            focusEditText()
            setAmountBudget()

            toggleCalendar.setOnCheckedChangeListener { toggle, isChecked -> // 캘린더 연동

                if (isChecked) {
                    tvWeekBudgetComment.text = resources.getString(R.string.week_budget_comment2)
                    tvWeekBudgetComment.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange
                        )
                    )
                    tvWeekBudgetFridayComment.visibility = View.VISIBLE
                    cvWeekBudgetFriday.visibility = View.VISIBLE
                    etWeekBudgetFridayBudget.setText(55000.toWon())
                    etWeekBudgetFridayBudget.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange
                        )
                    )
                    tvWeekBudgetSaturdayComment.visibility = View.VISIBLE
                    cvWeekBudgetSaturday.visibility = View.VISIBLE
                    etWeekBudgetSaturdayBudget.setText(80000.toWon())
                    etWeekBudgetSaturdayBudget.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange
                        )
                    )
                    tvWeekBudgetSundayComment.visibility = View.VISIBLE
                    cvWeekBudgetSunday.visibility = View.VISIBLE
                    etWeekBudgetSundayBudget.setText(40000.toWon())
                    etWeekBudgetSundayBudget.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.orange
                        )
                    )

                } else {
                    tvWeekBudgetComment.text = resources.getString(R.string.week_budget_comment)
                    tvWeekBudgetComment.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.nh_green
                        )
                    )
                    tvWeekBudgetFridayComment.visibility = View.GONE
                    etWeekBudgetFridayBudget.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.nh_green
                        )
                    )
                    cvWeekBudgetFriday.visibility = View.INVISIBLE
                    tvWeekBudgetSaturdayComment.visibility = View.GONE
                    cvWeekBudgetSaturday.visibility = View.INVISIBLE
                    etWeekBudgetSaturdayBudget.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.nh_green
                        )
                    )
                    tvWeekBudgetSundayComment.visibility = View.GONE
                    cvWeekBudgetSunday.visibility = View.INVISIBLE
                    etWeekBudgetSundayBudget.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.nh_green
                        )
                    )
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
                            is WeekBudgetAction.ShowBudgetDialog -> {
                                val budget = getBudgetInfo()

                                showAlertDialog(
                                    dialog = AlertDialog(
                                        mainActivity,
                                        title = "예산 설정",
                                        content = "입력한 예산으로 설정하시겠어요?"
                                    ) {
                                        binding.vm!!.setBudgetInfo(budget)
                                    }, viewLifecycleOwner
                                )
                            }
                            is WeekBudgetAction.NavigateHomeView -> navigate(WeekBudgetFragmentDirections.actionWeekBudgetFragmentToHomeFragment())
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
                        // 마이데이터 자산 연동
                        if (!state.isMyDataLinked) showLoading(dialog = LoadingDialog(mainActivity))
                        else setMyDataBudgetInfo(state)

                    }
                }
            }
        }
    }

    // 마이데이터 자산 연동 값 세팅
    private fun setMyDataBudgetInfo(state: WeekBudgetState) {
        binding.apply {
            if (state.budget.isNotEmpty()) {
                tvWeekBudgetAmount.text = state.sumBudget
                etWeekBudgetMondayBudget.setText(state.budget[0].toWon())
                etWeekBudgetTuesdayBudget.setText(state.budget[1].toWon())
                etWeekBudgetWednesdayBudget.setText(state.budget[2].toWon())
                etWeekBudgetThursdayBudget.setText(state.budget[3].toWon())
                etWeekBudgetFridayBudget.setText(state.budget[4].toWon())
                etWeekBudgetSaturdayBudget.setText(state.budget[5].toWon())
                etWeekBudgetSundayBudget.setText(state.budget[6].toWon())
            }
        }
    }

    // editText 포커스될 때 "원"이 있을시 초기화 하는 함수
    private fun focusEditText() {
        binding.apply {
            etWeekBudgetMondayBudget.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && binding.etWeekBudgetMondayBudget.text.toString().contains("원")) {
                    etWeekBudgetMondayBudget.text?.clear()
                }
            }

            etWeekBudgetTuesdayBudget.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && binding.etWeekBudgetTuesdayBudget.text.toString().contains("원")) {
                    etWeekBudgetTuesdayBudget.text?.clear()
                }
            }

            etWeekBudgetWednesdayBudget.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && binding.etWeekBudgetWednesdayBudget.text.toString().contains("원")) {
                    etWeekBudgetWednesdayBudget.text?.clear()
                }
            }

            etWeekBudgetThursdayBudget.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && binding.etWeekBudgetThursdayBudget.text.toString().contains("원")) {
                    etWeekBudgetThursdayBudget.text?.clear()
                }
            }

            etWeekBudgetFridayBudget.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && binding.etWeekBudgetFridayBudget.text.toString().contains("원")) {
                    etWeekBudgetFridayBudget.text?.clear()
                }
            }

            etWeekBudgetSaturdayBudget.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && binding.etWeekBudgetSaturdayBudget.text.toString().contains("원")) {
                    etWeekBudgetSaturdayBudget.text?.clear()
                }
            }

            etWeekBudgetSundayBudget.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus && binding.etWeekBudgetSundayBudget.text.toString().contains("원")) {
                    etWeekBudgetSundayBudget.text?.clear()
                }
            }
        }
    }

    // 총 예산 실시간 반영
    private fun setAmountBudget() {
        binding.apply {
            etWeekBudgetMondayBudget.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    val amount = getBudgetInfo().sum()
                    tvWeekBudgetAmount.text = "총 ${amount.toWon()}"
                }
            })

            etWeekBudgetTuesdayBudget.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    val amount = getBudgetInfo().sum()
                    tvWeekBudgetAmount.text = "총 ${amount.toWon()}"
                }
            })

            etWeekBudgetWednesdayBudget.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    val amount = getBudgetInfo().sum()
                    tvWeekBudgetAmount.text = "총 ${amount.toWon()}"
                }
            })

            etWeekBudgetThursdayBudget.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    val amount = getBudgetInfo().sum()
                    tvWeekBudgetAmount.text = "총 ${amount.toWon()}"
                }
            })

            etWeekBudgetFridayBudget.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    val amount = getBudgetInfo().sum()
                    tvWeekBudgetAmount.text = "총 ${amount.toWon()}"
                }
            })

            etWeekBudgetSaturdayBudget.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    val amount = getBudgetInfo().sum()
                    tvWeekBudgetAmount.text = "총 ${amount.toWon()}"
                }
            })

            etWeekBudgetSundayBudget.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    val amount = getBudgetInfo().sum()
                    tvWeekBudgetAmount.text = "총 ${amount.toWon()}"
                }
            })
        }
    }

    // 예산값 숫자로 변환
    private fun getBudgetInfo(): IntArray {
        val budget = IntArray(7)

        binding.apply {
            for (i in budget.indices) {
                when (i) {
                    0 -> budget[i] = TextUtil.getEditTextValueAsInt(etWeekBudgetMondayBudget)
                    1 -> budget[i] = TextUtil.getEditTextValueAsInt(etWeekBudgetTuesdayBudget)
                    2 -> budget[i] = TextUtil.getEditTextValueAsInt(etWeekBudgetWednesdayBudget)
                    3 -> budget[i] = TextUtil.getEditTextValueAsInt(etWeekBudgetThursdayBudget)
                    4 -> budget[i] = TextUtil.getEditTextValueAsInt(etWeekBudgetFridayBudget)
                    5 -> budget[i] = TextUtil.getEditTextValueAsInt(etWeekBudgetSaturdayBudget)
                    6 -> budget[i] = TextUtil.getEditTextValueAsInt(etWeekBudgetSundayBudget)
                }
            }
        }

        return budget
    }
}