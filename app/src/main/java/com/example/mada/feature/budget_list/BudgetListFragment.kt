package com.example.mada.feature.budget_list

import android.content.Context
import android.view.View
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.imaginativeworld.whynotimagecarousel.utils.dpToPx

private const val TAG = "DX"

@AndroidEntryPoint
class BudgetListFragment : BaseFragment<FragmentBudgetListBinding, BudgetListViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_budget_list
    override val viewModel: BudgetListViewModel by viewModels()

    private lateinit var mainActivity: MainActivity
    private val images =
        listOf(CarouselItem(R.drawable.ic_budget_binder), CarouselItem(R.drawable.ic_save_binder))

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        with(binding) {
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
                    if (position == 0) {
                        carouselBinderList.carouselPaddingEnd = 80.dpToPx(requireContext())
                        carouselBinderList.carouselPaddingStart = 30.dpToPx(requireContext())
                    } else {
                        carouselBinderList.carouselPaddingEnd = 30.dpToPx(requireContext())
                        carouselBinderList.carouselPaddingStart = 80.dpToPx(requireContext())
                    }
                }

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                    position: Int,
                    carouselItem: CarouselItem?
                ) {
                    super.onScrolled(recyclerView, dx, dy, position, carouselItem)
                    if (position == 0) {
                        tvBinderListType.text =
                            resources.getString(R.string.binder_list_week_binder)

                    } else tvBinderListType.text =
                        resources.getString(R.string.binder_list_save_binder)
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
                            is BudgetListAction.ShowToast -> showToast(action.content)
                            is BudgetListAction.ShowBudgetDialog -> {
//                                val budget = getBudgetInfo()
//
//                                showAlertDialog(
//                                    dialog = AlertDialog(
//                                        mainActivity,
//                                        title = "예산 설정",
//                                        content = "입력한 예산으로 설정하시겠어요?"
//                                    ) {
//                                        binding.vm!!.setBudgetInfo(budget)
//                                    }, viewLifecycleOwner
//                                )
                            }

                            is BudgetListAction.NavigateHomeView -> {}
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
}