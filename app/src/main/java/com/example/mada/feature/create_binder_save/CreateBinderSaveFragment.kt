package com.example.mada.feature.create_CreateBinder_save

import android.content.Context
import android.text.Editable
import android.util.Log
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentCreateBinderSaveBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.feature.budget_list.BudgetListFragmentDirections
import com.example.mada.util.DateUtil
import com.example.mada.util.TextUtil.setColoredSubstrings
import com.example.mada.util.TextUtil.toWon
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "DX"

@AndroidEntryPoint
class CreateBinderSaveFragment :
    BaseFragment<FragmentCreateBinderSaveBinding, CreateBinderSaveViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_create_binder_save
    override val viewModel: CreateBinderSaveViewModel by viewModels()

    private lateinit var mainActivity: MainActivity

    private var amount: Int = 0
    private var selectDate: Long = 0
    private var savingAmountPerMonth: Int = 0
    private var targetPeriod: String = ""
    private var ranges: List<String> = listOf()


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

            toolbarCreateBinderSave.setNavigationOnClickListener {
                backNavigate()
            }

            etCreateBinderSaveTargetAmount.doOnTextChanged { text, start, before, count ->
                setTargetAmount(text)
            }

            etCreateBinderSaveTargetPeriod.setOnClickListener {
                setDatePicker()
            }
        }
    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collect { action ->
                        when (action) {
                            is CreateBinderSaveAction.ShowToast -> showToast(action.content)
                            is CreateBinderSaveAction.ShowCreateSaveBinderDialog -> {
                                binding.apply {
                                    if (etCreateBinderSaveName.text.isNullOrEmpty() || etCreateBinderSaveTargetAmount.text.isNullOrEmpty() || etCreateBinderSaveTargetPeriod.text.isNullOrEmpty()) {
                                        showToast("모든 정보를 입력해 주세요!")
                                    } else {
                                        showAlertDialog(
                                            dialog = AlertDialog(
                                                mainActivity,
                                                title = resources.getString(R.string.binder_budget_create_binder_create_binder),
                                                content = resources.getString(R.string.binder_budget_create_binder_create_binder_comment)
                                            ) {
                                                viewModel.createSaveBinder(
                                                    name = etCreateBinderSaveName.text.toString(),
                                                    targetAmount = amount.toWon(),
                                                    targetPeriod = targetPeriod
                                                )

                                            }, viewLifecycleOwner
                                        )
                                    }
                                }
                            }

                            is CreateBinderSaveAction.NavigateBinderSaveView -> {
                                showToast("저축 바인더를 생성했어요!")
                                navigate(
                                    CreateBinderSaveFragmentDirections.actionCreateBinderSaveFragmentToBinderSaveFragment()
                                )
                            }
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

    private fun setTargetAmount(targetAmount: CharSequence?) {
        binding.apply {
            amount =
                if (targetAmount.isNullOrEmpty()) 0 else targetAmount.toString().toInt()

            savingAmountPerMonth = DateUtil.getSavingAmountPerMonth(amount, selectDate)

            ranges = listOf(
                etCreateBinderSaveTargetPeriod.text.toString(),
                ("월"),
                savingAmountPerMonth.toWon()
            )

            if (selectDate != 0L) {
                tvCreateBinderSaveTargetComment.setColoredSubstrings(
                    "${etCreateBinderSaveTargetPeriod.text.toString()}까지\n 월 ${
                        savingAmountPerMonth.toWon()
                    } 저축이 필요해요.", ranges, colorRes = R.color.nh_green
                )
            }
        }
    }

    private fun setDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now()) // 오늘 이후만 선택 가능
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("목표 기간 선택")
            .setTheme(R.style.CustomDatePickerTheme)
            .setCalendarConstraints(constraintsBuilder)
            .build()

        picker.show(parentFragmentManager, picker.toString())

        setSavingComment(picker, binding.etCreateBinderSaveTargetAmount.text)
    }

    private fun setSavingComment(picker: MaterialDatePicker<Long>, targetAmount: Editable?) {
        binding.apply {
            picker.addOnPositiveButtonClickListener { selection ->
                val date = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(
                    Date(
                        selection
                    )
                )
                etCreateBinderSaveTargetPeriod.setText(date)
                targetPeriod = date

                amount =
                    if (targetAmount.isNullOrEmpty()) 0 else etCreateBinderSaveTargetAmount.text.toString()
                        .toInt()
                selectDate = selection

                savingAmountPerMonth = DateUtil.getSavingAmountPerMonth(amount, selectDate)

                ranges = listOf(
                    etCreateBinderSaveTargetPeriod.text.toString(),
                    ("월"),
                    savingAmountPerMonth.toWon()
                )

                tvCreateBinderSaveTargetComment.setColoredSubstrings(
                    "${etCreateBinderSaveTargetPeriod.text.toString()}까지\n 월 ${
                        savingAmountPerMonth.toWon()
                    } 저축이 필요해요.", ranges, colorRes = R.color.nh_green
                )
            }
        }
    }
}