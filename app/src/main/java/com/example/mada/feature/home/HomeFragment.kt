package com.example.mada.feature.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.marginEnd
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentHomeBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.feature.budget_list.BudgetListFragmentDirections
import com.example.mada.util.BudgetUtil
import com.example.mada.util.TextUtil.toWon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

private const val TAG = "DX"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModels()

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
                                    title = resources.getString(R.string.home_create_account_link),
                                    content = resources.getString(R.string.home_recommend_create_account)
                                ) {
                                    navigate(
                                        HomeFragmentDirections.actionHomeFragmentToAccountLinkFragment()
                                    )
                                }, viewLifecycleOwner
                            )

                            is HomeAction.NavigateWeekSavingView -> {
                                binding.apply {
                                    if (!viewModel.state.value.isSigned) {
                                        showAlertDialog(
                                            dialog = AlertDialog(
                                                mainActivity,
                                                title = resources.getString(R.string.home_create_account_link),
                                                content = resources.getString(R.string.home_recommend_create_account)
                                            ) {
                                                navigate(
                                                    HomeFragmentDirections.actionHomeFragmentToAccountLinkFragment()
                                                )
                                            }, viewLifecycleOwner
                                        )
                                    }else {
                                        if (viewModel.state.value.isBudgetExist) {
                                            if (!binding.vm!!.state.value.isSaveAble) showToast(
                                                resources.getString(
                                                    R.string.home_save_available
                                                )
                                            ) else if (!viewModel.state.value.isSaveBudgetExist) showAlertDialog(
                                                dialog = AlertDialog(
                                                    mainActivity,
                                                    title = resources.getString(R.string.binder_budget_create_binder_save),
                                                    content = resources.getString(R.string.binder_budget_create_binder_save_comment)
                                                ) {
                                                    navigate(HomeFragmentDirections.actionHomeFragmentToCreateBinderSaveFragment())
                                                }, viewLifecycleOwner
                                            )
                                            else navigate(HomeFragmentDirections.actionHomeFragmentToWeekSavingFragment())
                                        }
                                        else showAlertDialog(
                                            dialog = AlertDialog(
                                                mainActivity,
                                                title = resources.getString(R.string.home_set_budget),
                                                content = resources.getString(R.string.home_set_budget_comment),
                                            ) {
                                                navigate(
                                                    HomeFragmentDirections.actionHomeFragmentToWeekBudgetFragment()
                                                )
                                            }, viewLifecycleOwner
                                        )
                                    }
                                }
                            }

                            is HomeAction.NavigateBinderListView -> {
                                if (!binding.vm!!.state.value.isSigned) {
                                    showAlertDialog(
                                        dialog = AlertDialog(
                                            mainActivity,
                                            title = resources.getString(R.string.home_create_account_link),
                                            content = resources.getString(R.string.home_recommend_create_account)
                                        ) {
                                            navigate(
                                                HomeFragmentDirections.actionHomeFragmentToAccountLinkFragment()
                                            )
                                        }, viewLifecycleOwner
                                    )
                                } else navigate(HomeFragmentDirections.actionHomeFragmentToBudgetListFragment())
                            }

                            is HomeAction.ShowCreateBudgetDialog -> {
                                binding.apply {
                                    if (!viewModel.state.value.isSigned) {
                                        showAlertDialog(
                                            dialog = AlertDialog(
                                                mainActivity,
                                                title = resources.getString(R.string.home_create_account_link),
                                                content = resources.getString(R.string.home_recommend_create_account)
                                            ) {
                                                navigate(
                                                    HomeFragmentDirections.actionHomeFragmentToAccountLinkFragment()
                                                )
                                            }, viewLifecycleOwner
                                        )
                                    }else {
                                        if (viewModel.state.value.isBudgetExist) {
                                            navigate(HomeFragmentDirections.actionHomeFragmentToBinderBudgetFragment())
                                        }
                                        else showAlertDialog(
                                            dialog = AlertDialog(
                                                mainActivity,
                                                title = resources.getString(R.string.home_set_budget),
                                                content = resources.getString(R.string.home_set_budget_comment),
                                            ) {
                                                navigate(
                                                    HomeFragmentDirections.actionHomeFragmentToWeekBudgetFragment()
                                                )
                                            }, viewLifecycleOwner
                                        )
                                    }
                                }
                            }
                            is HomeAction.NavigateHomeDetailFragment -> navigate(HomeFragmentDirections.actionHomeFragmentToHomeShareFragment())
//                            {
//                                when(viewModel.state.value.step) {
//                                    0 -> {
//                                        if (!binding.vm!!.state.value.isSigned) {
//                                            showAlertDialog(
//                                                dialog = AlertDialog(
//                                                    mainActivity,
//                                                    title = resources.getString(R.string.home_create_account_link),
//                                                    content = resources.getString(R.string.home_recommend_create_account)
//                                                ) {
//                                                    navigate(
//                                                        HomeFragmentDirections.actionHomeFragmentToAccountLinkFragment()
//                                                    )
//                                                }, viewLifecycleOwner
//                                            )
//                                        } else showToast("한 주 챌린지 완료후 이동할 수 있어요 \uD83D\uDE03")
//                                    }
//                                    else -> navigate(HomeFragmentDirections.actionHomeFragmentToHomeDetailFragment())
//                                }
//                            }
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
                        setViewByStep(state)
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

    // 오늘 쓸 수 있는 돈 + 남은 금액에 따른 % 컬러 변경
    private fun setTodayMoneyLeft(state: HomeState) {
        binding.apply {
            val progress =
                (((state.budget[state.today] - BudgetUtil.expenditure[state.today]).toDouble() / state.budget[state.today].toDouble()) * 100).roundToInt()

            if (progress < 50) {
                tvHomeTodayProgress.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.red
                    )
                )
            }
        }
    }

    // 주차별 화면 UI적용
    private fun setViewByStep(state: HomeState) {
        binding.apply {
            when (state.step) {
                0 -> { // 둘러보기 화면
                    if (!state.isBudgetExist) ivHomeCreateBudget.visibility = View.VISIBLE
                    ivHomeShare.visibility = View.INVISIBLE
                }
                1 -> { // 1주차
                    ivHome.setImageResource(R.drawable.image_home_one_week)
                }
                2 -> { // 2주차
                    ivHome.setImageResource(R.drawable.image_home_two_week)
                }
                else -> {
                    val homeParams = ivHome.layoutParams as ViewGroup.MarginLayoutParams
                    homeParams.apply {
                        marginStart = 0
                        marginEnd = 0
                    }

                    ivHome.layoutParams = homeParams
                    ivHome.setImageResource(R.drawable.image_home_last_week)

                    val params = ivHomeShare.layoutParams as ViewGroup.MarginLayoutParams
                    params.marginEnd = 80

                    ivHomeShare.layoutParams = params
                }
            }
        }
    }
}