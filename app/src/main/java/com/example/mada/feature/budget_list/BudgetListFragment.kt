package com.example.mada.feature.budget_list

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.helper.widget.Carousel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentBudgetListBinding
import com.example.mada.dialog.AlertDialog
import com.example.mada.util.ImageUtil.changeItemWithFade
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.imaginativeworld.whynotimagecarousel.utils.dpToPx
import org.imaginativeworld.whynotimagecarousel.utils.pxToDp
import kotlin.math.log

private const val TAG = "DX"

@AndroidEntryPoint
class BudgetListFragment : BaseFragment<FragmentBudgetListBinding, BudgetListViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_budget_list
    override val viewModel: BudgetListViewModel by viewModels()

    private var images = mutableListOf<CarouselItem>()
    private var currentPosition = 0
    private var isImageChanged: Boolean = false

    override fun initView() {
        with(binding) {
            carouselBinderList.setIndicator(indicatorBinderList)
            carouselBinderList.registerLifecycle(viewLifecycleOwner)

            images = mutableListOf(
                CarouselItem(viewModel.state.value.budgetBinderImage),
                CarouselItem(viewModel.state.value.saveBinderImage)
            )
            carouselBinderList.setData(images)
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
                    currentPosition = position
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
                }
            }

            carouselBinderList.carouselListener = object : CarouselListener {
                override fun onClick(position: Int, carouselItem: CarouselItem) {
                    navigateBinderDetailFragment(position)
                    isImageChanged = false
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
                            is BudgetListAction.ShowSetBinderImageAlert -> showAlertDialog(
                                dialog = AlertDialog(
                                    mainActivity,
                                    title = resources.getString(R.string.binder_list_set_image),
                                    content = resources.getString(R.string.binder_list_set_image_comment)
                                ) {
                                    when (action.image) {
                                        "cloud" -> viewModel.setBinderImage(
                                            currentPosition,
                                            R.drawable.binder_cloud
                                        )

                                        "heart" -> viewModel.setBinderImage(
                                            currentPosition,
                                            R.drawable.binder_heart
                                        )

                                        "green" -> viewModel.setBinderImage(
                                            currentPosition,
                                            R.drawable.binder_green
                                        )

                                        "alle" -> viewModel.setBinderImage(
                                            currentPosition,
                                            R.drawable.binder_alle
                                        )

                                        "onee" -> viewModel.setBinderImage(
                                            currentPosition,
                                            R.drawable.binder_onee
                                        )

                                        "ribbon" -> viewModel.setBinderImage(
                                            currentPosition,
                                            R.drawable.binder_ribbon
                                        )
                                    }
                                }, viewLifecycleOwner
                            )

                            is BudgetListAction.SetBinderImage -> {
                                binding.apply {
                                    carouselBinderList.changeItemWithFade(
                                        images,
                                        currentPosition,
                                        action.image
                                    )
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
                    viewModel.state.collect { state ->

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