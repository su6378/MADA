package com.example.mada.feature.budget_list

import android.content.Context
import android.util.Log
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
import com.example.mada.dialog.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.imaginativeworld.whynotimagecarousel.utils.dpToPx

private const val TAG = "DX"

@AndroidEntryPoint
class BudgetListFragment : BaseFragment<FragmentBudgetListBinding, BudgetListViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_budget_list
    override val viewModel: BudgetListViewModel by viewModels()

    private val images =
        listOf(CarouselItem(R.drawable.ic_budget_binder), CarouselItem(R.drawable.ic_save_binder))

    override fun initView() {
        with(binding) {
            carouselBinderList.setData(images)
            carouselBinderList.next()
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
                    setBinderPadding(position)
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

    // 선택된 바인더에 따른 패딩 조절
    private fun setBinderPadding(position: Int) {
        binding.apply {
            if (position == 0) {
                carouselBinderList.carouselPaddingEnd = 80.dpToPx(requireContext())
                carouselBinderList.carouselPaddingStart = 30.dpToPx(requireContext())
            } else {
                carouselBinderList.carouselPaddingEnd = 30.dpToPx(requireContext())
                carouselBinderList.carouselPaddingStart = 80.dpToPx(requireContext())
            }
        }
    }

    // 선택된 바인더 UI 세팅
    private fun setBiderType(position: Int) {
        binding.apply {
            if (position == 0) {
                tvBinderListType.text =
                    resources.getString(R.string.binder_list_week_binder)
                ivBinderListCloud.setImageResource(R.drawable.ic_binder_cloud)
                ivBinderListHeart.setImageResource(R.drawable.ic_binder_heart)
                ivBinderListAlle.setImageResource(R.drawable.ic_binder_alle)
                ivBinderListOnee.setImageResource(R.drawable.ic_binder_onee)
                ivBinderListGreen.setImageResource(R.drawable.ic_binder_green)

            } else tvBinderListType.text =
                resources.getString(R.string.binder_list_save_binder)
            ivBinderListCloud.setImageResource(R.drawable.ic_binder_cloud)
            ivBinderListHeart.setImageResource(R.drawable.ic_binder_heart)
            ivBinderListAlle.setImageResource(R.drawable.ic_binder_alle)
            ivBinderListOnee.setImageResource(R.drawable.ic_binder_onee)
            ivBinderListGreen.setImageResource(R.drawable.ic_binder_green)
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
                }else navigate(BudgetListFragmentDirections.actionBudgetListFragmentToBinderBudgetFragment())
            }else {
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