package com.example.mada.feature.home

import android.content.Context
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentHomeBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.util.BudgetUtil
import com.example.mada.util.TextUtil.toWon
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

        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
            toolbarHome.setNavigationOnClickListener {
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
                            is HomeAction.ShowToast -> showAlertDialog(
                                dialog = AlertDialog(
                                    mainActivity
                                ) { navigate(HomeFragmentDirections.actionHomeFragmentToAccountFragment()) },
                                viewLifecycleOwner
                            )

                            is HomeAction.NavigateWeekBudgetView -> navigate(HomeFragmentDirections.actionHomeFragmentToWeekBudgetFragment())
                            is HomeAction.NavigateMoneyLeftView -> navigate(HomeFragmentDirections.actionHomeFragmentToMoneyLeftFragment())
                            is HomeAction.NavigateWeekSavingView -> {
                                if (!binding.vm!!.state.value.isSaveAble) showToast(resources.getString(R.string.home_save_available))
                                else navigate(HomeFragmentDirections.actionHomeFragmentToWeekSavingFragment())
                            }
                            is HomeAction.NavigateOnBoardingView -> showAlertDialog(
                                dialog = AlertDialog(
                                    mainActivity
                                ) { navigate(HomeFragmentDirections.actionHomeFragmentToAccountFragment()) },
                                viewLifecycleOwner
                            )
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
                        when (state.today) {
                            0 -> binding.cvHomeMonday.visibility = View.VISIBLE
                            1 -> binding.cvHomeTuesday.visibility = View.VISIBLE
                            2 -> binding.cvHomeWednesday.visibility = View.VISIBLE
                            3 -> binding.cvHomeThursday.visibility = View.VISIBLE
                            4 -> binding.cvHomeFriday.visibility = View.VISIBLE
                            5 -> binding.cvHomeSaturday.visibility = View.VISIBLE
                            6 -> binding.cvHomeSunday.visibility = View.VISIBLE
                        }

                        if (state.isSigned) { // 계좌 개설을 한 경우
                            if (state.isBudgetExist) { // 예산 설정을 한 경우
                                binding.apply {
                                    cvHomeInitial.visibility = View.INVISIBLE
                                    ivHome.visibility = View.VISIBLE
                                    "${(state.budget[state.today] - BudgetUtil.expenditure[state.today]).toWon()} 남았어요!".also {
                                        tvTodayLeftContent.text = it
                                    }
                                    tvHomeBuildComment.text =
                                        resources.getString(R.string.money_diary_build_comment)
                                    ivBalloon.setImageResource(R.drawable.ic_balloon)
                                }
                            } else {
                                binding.apply {
                                    showAlertDialog(
                                        dialog = AlertDialog(
                                            mainActivity,
                                            title = "예산 설정 하러 가기",
                                            content = "예산이 설정되어 있지 않아요. 예산 설정 화면으로 이동하시겠어요?"
                                        ) {
                                            navigate(
                                                HomeFragmentDirections.actionHomeFragmentToWeekBudgetFragment()
                                            )
                                        }, viewLifecycleOwner
                                    )
                                    cvHomeInitial.visibility = View.INVISIBLE
                                    ivHome.visibility = View.VISIBLE
                                    tvTodayLeftContent.text =
                                        resources.getString(R.string.all_one_mada_need_budget)
                                    tvHomeBuildComment.text =
                                        resources.getString(R.string.money_diary_build_comment_before_challenge)
                                    ivBalloon.setImageResource(R.drawable.ic_balloon)
                                }
                            }
                        } else {
                            binding.apply {
                                cvHomeInitial.visibility = View.VISIBLE
                                ivHome.visibility = View.INVISIBLE
                                tvTodayLeftContent.text =
                                    resources.getString(R.string.money_diary_build_comment_initial)
                                tvHomeBuildComment.text =
                                    resources.getString(R.string.money_diary_build_comment_initial)
                                ivBalloon.setImageResource(R.drawable.ic_initial_balloon)
                            }
                        }
                    }
                }
            }
        }
    }
}