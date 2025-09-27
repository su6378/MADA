package com.example.mada.feature.create_binder_save

import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentCreateBinderSaveBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.util.DateUtil
import com.example.mada.util.ImageUtil.changeImageWithFade
import com.example.mada.util.TextUtil.setSizedSubstringsSp
import com.example.mada.util.TextUtil.toWon
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class CreateBinderSaveFragment :
    BaseFragment<FragmentCreateBinderSaveBinding, CreateBinderSaveViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_create_binder_save
    override val viewModel: CreateBinderSaveViewModel by viewModels()

    private var amount: Int = 0
    private var month: Int = 12
    private var ranges: List<String> = listOf()

    override fun initView() {
        with(binding) {
            mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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
        }
    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collect { action ->
                        binding.apply {
                            when (action) {
                                is CreateBinderSaveAction.ShowCreateSaveBinderDialog -> {
                                    binding.apply {
                                        if (etCreateBinderSaveName.text.isNullOrEmpty()) {
                                            showToast("모든 정보를 입력해 주세요!")
                                        } else {
                                            showAlertDialog(
                                                dialog = AlertDialog(
                                                    mainActivity,
                                                    title = resources.getString(R.string.binder_budget_create_binder_create_binder),
                                                    content = resources.getString(R.string.binder_budget_create_binder_create_binder_comment)
                                                ) {
                                                    val dayInfo = DateUtil.getTodayAndFutureDate(month)
                                                    viewModel.createSaveBinder(
                                                        name = etCreateBinderSaveName.text.toString(),
                                                        targetAmount = amount.toWon(),
                                                        startPeriod =  dayInfo.first,
                                                        targetPeriod = dayInfo.second
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

                                is CreateBinderSaveAction.SetSaveBinderImage -> viewModel.setBinderImage(
                                    action.image
                                )

                                CreateBinderSaveAction.ShowNumberPicker -> showDateDialog()
                            }
                        }
                    }
                }

                launch {
                    viewModel.state.collectLatest { state ->
                        binding.apply {
                            when (state.saveBinderImage) {
                                "cloud" -> ivCreateBinderSave.changeImageWithFade(R.drawable.binder_cloud)
                                "heart" -> ivCreateBinderSave.changeImageWithFade(R.drawable.binder_heart)
                                "green" -> ivCreateBinderSave.changeImageWithFade(R.drawable.binder_green)
                                "alle" -> ivCreateBinderSave.changeImageWithFade(R.drawable.binder_alle)
                                "onee" -> ivCreateBinderSave.changeImageWithFade(R.drawable.binder_onee)
                                "ribbon" -> ivCreateBinderSave.changeImageWithFade(R.drawable.binder_ribbon)
                            }
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
        }
    }

    private fun setTargetAmount(targetAmount: CharSequence?) {
        binding.apply {
            amount = if (targetAmount.isNullOrEmpty()) 0 else targetAmount.toString().toInt()

            tvCreateBinderSaveWon.visibility = View.VISIBLE

            val info = DateUtil.getSavingPlan(amount, month)

            ranges = listOf(info.second)

            tvCreateBinderSaveTargetComment.setSizedSubstringsSp(
                info.first, ranges, 24, colorRes = R.color.nh_green
            )
        }
    }

    private fun showDateDialog() {
        val options = (1..24).map { "${it}개월" }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("적금기간")
            .setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, options)
            ) { dialogInterface, which ->
                val selected = options[which]
                month = which + 1
                binding.tvCreateBinderPeriod.text = selected
                setSavingComment(which + 1)
            }
            .create()

        dialog.show()

        // 다이얼로그 크기 제한 (예: 높이를 500dp 정도로)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (500 * resources.displayMetrics.density).toInt()
        )
    }

    private fun setSavingComment(month: Int) {
        binding.apply {
            amount =
                if (etCreateBinderSaveTargetAmount.text.isNullOrEmpty()) 0 else etCreateBinderSaveTargetAmount.text.toString()
                    .toInt()

            val info = DateUtil.getSavingPlan(amount, month)

            ranges = listOf(info.second)

            tvCreateBinderSaveTargetComment.setSizedSubstringsSp(
                info.first, ranges, 24, colorRes = R.color.nh_green
            )
        }
    }
}