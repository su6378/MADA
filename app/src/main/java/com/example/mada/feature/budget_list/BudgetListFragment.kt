package com.example.mada.feature.budget_list

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentBudgetListBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.util.ImageUtil.changeItemWithFade
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

private const val TAG = "DX"

@AndroidEntryPoint
class BudgetListFragment : BaseFragment<FragmentBudgetListBinding, BudgetListViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_budget_list
    override val viewModel: BudgetListViewModel by viewModels()

    private var images = mutableListOf<CarouselItem>()
    private var currentPosition = 0

    override fun initView() {
        with(binding) {
            val budgetBinderImage =
                when (viewModel.state.value.budgetBinderImage) {
                    "cloud" -> R.drawable.binder_cloud
                    "heart" -> R.drawable.binder_heart
                    "green" -> R.drawable.binder_green
                    "onee" ->  R.drawable.binder_onee
                    "ribbon" -> R.drawable.binder_ribbon
                    else -> R.drawable.binder_alle
                }

            val saveBinderImage =
                when (viewModel.state.value.saveBinderImage) {
                    "cloud" -> R.drawable.binder_cloud
                    "heart" -> R.drawable.binder_heart
                    "green" -> R.drawable.binder_green
                    "alle" -> R.drawable.binder_alle
                    "ribbon" -> R.drawable.binder_ribbon
                    else ->  R.drawable.binder_onee
                }

            images = mutableListOf(
                CarouselItem(budgetBinderImage),
                CarouselItem(saveBinderImage)
            )

            carouselBinderList.setData(images)
            carouselBinderList.setIndicator(indicatorBinderList)
            carouselBinderList.registerLifecycle(viewLifecycleOwner)
        }
    }

    override fun initDataBinding() {
        binding.apply {
            vm = viewModel

            toolbarBinderList.setNavigationOnClickListener {
                backNavigate()
            }

            carouselBinderList.onScrollListener = object : CarouselOnScrollListener {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int,
                    position: Int,
                    carouselItem: CarouselItem?
                ) {
                    super.onScrollStateChanged(recyclerView, newState, position, carouselItem)
                    binding.indicatorBinderList.animatePageSelected(currentPosition)
                }

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                    position: Int,
                    carouselItem: CarouselItem?
                ) {
                    super.onScrolled(recyclerView, dx, dy, position, carouselItem)
                    setBiderType(position)
                    currentPosition = position
                }
            }

            carouselBinderList.carouselListener = object : CarouselListener {
                override fun onClick(position: Int, carouselItem: CarouselItem) {
                    navigateBinderDetailFragment(position)
                }
            }
        }
    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collectLatest { action ->
                        when (action) {
                            is BudgetListAction.ShowSetBinderImageAlert -> showAlertDialog(
                                dialog = AlertDialog(
                                    mainActivity,
                                    title = resources.getString(R.string.binder_list_set_image),
                                    content = resources.getString(R.string.binder_list_set_image_comment)
                                ) {
                                    Log.d(TAG, "initObserving: $currentPosition")
                                    viewModel.setBinderImage(currentPosition, action.image)
                                }, viewLifecycleOwner
                            )

                            is BudgetListAction.SetBinderImage -> {
                                binding.apply {
                                    when (action.image) {
                                        "cloud" -> carouselBinderList.changeItemWithFade(
                                            images,
                                            currentPosition,
                                            R.drawable.binder_cloud
                                        )

                                        "heart" -> carouselBinderList.changeItemWithFade(
                                            images,
                                            currentPosition,
                                            R.drawable.binder_heart
                                        )

                                        "green" -> carouselBinderList.changeItemWithFade(
                                            images,
                                            currentPosition,
                                            R.drawable.binder_green
                                        )

                                        "alle" -> carouselBinderList.changeItemWithFade(
                                            images,
                                            currentPosition,
                                            R.drawable.binder_alle
                                        )

                                        "onee" -> carouselBinderList.changeItemWithFade(
                                            images,
                                            currentPosition,
                                            R.drawable.binder_onee
                                        )

                                        "ribbon" -> carouselBinderList.changeItemWithFade(
                                            images,
                                            currentPosition,
                                            R.drawable.binder_ribbon
                                        )
                                    }
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

                launch {
                    viewModel.state.collectLatest { state ->

                    }
                }
            }
        }
    }

    // 선택된 바인더 UI 세팅
    private fun setBiderType(position: Int) {
        binding.apply {
            if (position == 0) {
                tvBinderListType.text =
                    resources.getString(R.string.binder_list_week_binder)

            } else tvBinderListType.text =
                resources.getString(R.string.binder_list_save_binder)
        }
    }

    // 해당 바인더 선택시 이동
    private fun navigateBinderDetailFragment(position: Int) {
        binding.apply {
            if (position == 0) {
                if (!viewModel.state.value.isBudgetExist) {
                    showAlertDialog(
                        dialog = AlertDialog(
                            mainActivity,
                            title = resources.getString(R.string.binder_list_navigate_budget),
                            content = resources.getString(R.string.binder_list_navigate_budget_comment)
                        ) {
                            navigate(BudgetListFragmentDirections.actionBudgetListFragmentToWeekBudgetFragment())
                        }, viewLifecycleOwner
                    )
                } else navigate(BudgetListFragmentDirections.actionBudgetListFragmentToBinderBudgetFragment())
            } else {
                if (viewModel.state.value.isSaveBinderExist) navigate(
                    BudgetListFragmentDirections.actionBudgetListFragmentToBinderSaveFragment()
                )
                else showAlertDialog(
                    dialog = AlertDialog(
                        mainActivity,
                        title = resources.getString(R.string.binder_budget_create_binder_save),
                        content = resources.getString(R.string.binder_budget_create_binder_save_comment)
                    ) {
                        navigate(BudgetListFragmentDirections.actionBudgetListFragmentToCreateBinderSaveFragment())
                    }, viewLifecycleOwner
                )
            }
        }
    }
}