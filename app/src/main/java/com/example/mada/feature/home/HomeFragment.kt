package com.example.mada.feature.home

import android.content.Context
import android.util.Log
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
                                    mainActivity,
                                    title = resources.getString(R.string.home_create_account),
                                    content = resources.getString(R.string.home_recommend_create_account)
                                ) {
                                    navigate(
                                        HomeFragmentDirections.actionHomeFragmentToAccountFragment()
                                    )
                                }, viewLifecycleOwner
                            )

                            is HomeAction.NavigateWeekBudgetView -> navigate(HomeFragmentDirections.actionHomeFragmentToWeekBudgetFragment())
                            is HomeAction.NavigateWeekSavingView -> {
                                if (!binding.vm!!.state.value.isSaveAble) showToast(
                                    resources.getString(
                                        R.string.home_save_available
                                    )
                                )
                                else navigate(HomeFragmentDirections.actionHomeFragmentToWeekSavingFragment())
                            }

                            is HomeAction.NavigateOnBoardingView -> showAlertDialog(
                                dialog = AlertDialog(
                                    mainActivity
                                ) { navigate(HomeFragmentDirections.actionHomeFragmentToAccountFragment()) },
                                viewLifecycleOwner
                            )

                            is HomeAction.NavigateBinderListView -> {
                                if (!binding.vm!!.state.value.isSigned) {
                                    showAlertDialog(
                                        dialog = AlertDialog(
                                            mainActivity,
                                            title = resources.getString(R.string.home_create_account),
                                            content = resources.getString(R.string.home_recommend_create_account)
                                        ) {
                                            navigate(
                                                HomeFragmentDirections.actionHomeFragmentToAccountFragment()
                                            )
                                        }, viewLifecycleOwner
                                    )
                                } else navigate(HomeFragmentDirections.actionHomeFragmentToBudgetListFragment())
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
                        setCalenderBackground(state)
                        setTodayMoneyLeft(state)
                        setCalendarHammer(state)

                        if (state.isSigned) { // 계좌 개설을 한 경우
                            if (state.isBudgetExist) { // 예산 설정을 한 경우
                                binding.apply {
                                    cvHomeInitial.visibility = View.INVISIBLE
                                    ivHome.visibility = View.VISIBLE

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
                                    tvHomeBuildComment.text =
                                        resources.getString(R.string.money_diary_build_comment_before_challenge)
                                    ivBalloon.setImageResource(R.drawable.ic_balloon)
                                }
                            }
                        } else {
                            binding.apply {
                                cvHomeInitial.visibility = View.VISIBLE
                                ivHome.visibility = View.INVISIBLE
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

    // 캘린더 해당 요일에 초록색 컬러 표시
    private fun setCalenderBackground(state: HomeState) {
        when (state.today) {
            0 -> binding.cvHomeMonday.visibility = View.VISIBLE
            1 -> binding.cvHomeTuesday.visibility = View.VISIBLE
            2 -> binding.cvHomeWednesday.visibility = View.VISIBLE
            3 -> binding.cvHomeThursday.visibility = View.VISIBLE
            4 -> binding.cvHomeFriday.visibility = View.VISIBLE
            5 -> binding.cvHomeSaturday.visibility = View.VISIBLE
            6 -> binding.cvHomeSunday.visibility = View.VISIBLE
        }
    }

    // 요일별 예산 초과 유무에 따른 망치 이미지
    private fun setCalendarHammer(state: HomeState) {
        val budget = state.budget
        val expenditure = BudgetUtil.expenditure

        binding.apply {
            for (i in budget.indices) {
                if (budget[i] - expenditure[i] >= 0) {
                    when (i) {
                        0 -> ivHomeMonday.visibility = View.VISIBLE
                        1 -> ivHomeTuesday.visibility = View.VISIBLE
                        2 -> ivHomeWednesday.visibility = View.VISIBLE
                        3 -> ivHomeThursday.visibility = View.VISIBLE
                        4 -> ivHomeFriday.visibility = View.VISIBLE
                        5 -> ivHomeSaturday.visibility = View.VISIBLE
                        6 -> ivHomeSunday.visibility = View.VISIBLE
                    }
                } else {
                    when (i) {
                        0 -> tvMondayDay.visibility = View.VISIBLE
                        1 -> tvTuesdayDay.visibility = View.VISIBLE
                        2 -> tvWednesdayDay.visibility = View.VISIBLE
                        3 -> tvThursdayDay.visibility = View.VISIBLE
                        4 -> tvFridayDay.visibility = View.VISIBLE
                        5 -> tvSaturdayDay.visibility = View.VISIBLE
                        6 -> tvSundayDay.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    // 오늘 쓸 수 있는 돈
    private fun setTodayMoneyLeft(state: HomeState) {
        binding.apply {
            if (state.isSigned) { // 계좌 개설을 한 경우
                if (state.isBudgetExist) { // 예산 설정한 경우
                    "${(state.budget[state.today] - BudgetUtil.expenditure[state.today]).toWon()} 남았어요!".also {
                        tvTodayLeftContent.text = it
                    }
                } else tvTodayLeftContent.text =
                    resources.getString(R.string.all_one_mada_need_budget)
            } else tvTodayLeftContent.text =
                resources.getString(R.string.money_diary_build_comment_initial)
        }
    }
}