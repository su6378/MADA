package com.example.mada.feature.allone

import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.Visibility
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentAllOneBinding
import com.example.mada.util.TextUtil
import com.example.mada.util.TextUtil.toWon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class AllOneFragment : BaseFragment<FragmentAllOneBinding, AllOneViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_all_one
    override val viewModel: AllOneViewModel by viewModels()

    private var isScroll = false

    override fun initView() {
        with(binding) {

        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
            svAllOne.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                //스크롤 아래로
                if (scrollY > oldScrollY) {
                    if (v.canScrollVertically(-1) && !isScroll) {
                        ivAllOneFab.visibility = View.INVISIBLE
                        ivAllOneBottomNavigation.visibility = View.INVISIBLE

                        ivAllOneFab.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.translate_down
                            )
                        )
                        ivAllOneBottomNavigation.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.translate_down
                            )
                        )
                    }

                    isScroll = true
                }

                // 스크롤 위로
                if (scrollY + 1 < oldScrollY) {
                    if (!v.canScrollVertically(-1)) { // 최상단 감지
                        ivAllOneFab.visibility = View.VISIBLE
                        ivAllOneBottomNavigation.visibility = View.VISIBLE

                        ivAllOneFab.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.translate_up
                            )
                        )
                        ivAllOneBottomNavigation.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.translate_up
                            )
                        )

                        isScroll = false
                    }
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
                            is AllOneAction.ShowToast -> showToast(action.content)
                            is AllOneAction.NavigateOnBoardingView -> {
                                navigate(AllOneFragmentDirections.actionAllOneFragmentToHomeFragment())
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

                launch {
                    viewModel.account.collect { account ->
                        if (account) {
                            binding.apply {
                                tvAllOneMada.text = resources.getString(R.string.all_one_mada_challenge)
                                ivAllOneMadaAccount.visibility = View.VISIBLE
                                tvAllOneMadaAccountMoney.visibility = View.VISIBLE
                                if (viewModel.money.value < 0) tvAllOneMadaAccount.text = resources.getString(R.string.all_one_mada_account_over_money)
                                else tvAllOneMadaAccount.text = resources.getString(R.string.all_one_mada_account_possible_money)
                            }
                        } else {
                            binding.apply {
                                tvAllOneMada.text = resources.getString(R.string.all_one_mada_no_challenge)
                                tvAllOneMadaAccount.text = resources.getString(R.string.all_one_mada_no_account)
                                ivAllOneMadaAccount.visibility = View.GONE
                                tvAllOneMadaAccountMoney.visibility = View.GONE
                            }
                        }
                    }
                }


                launch {
                    viewModel.money.collect { money ->
                        binding.tvAllOneMadaAccountMoney.text = money.toWon()
                    }
                }
            }
        }
    }
}