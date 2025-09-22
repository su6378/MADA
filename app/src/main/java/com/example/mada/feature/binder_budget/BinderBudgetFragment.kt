package com.example.mada.feature.binder_budget

import android.content.Context
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentBinderBudgetBinding
import com.example.mada.feature.home.HomeState
import com.example.mada.util.DateUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class BinderBudgetFragment : BaseFragment<FragmentBinderBudgetBinding, BinderBudgetViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_binder_budget
    override val viewModel: BinderBudgetViewModel by viewModels()

    private lateinit var mainActivity: MainActivity

    private val dropdownMenu = arrayListOf<String>()
    private var todayWeek: Int = 0

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

            toolbarBinderList.setNavigationOnClickListener {
                backNavigate()
            }

            dropdownMenuBinderBudget.setOnClickListener {
                dropdownMenuBinderBudget.showDropDown()
                dropdownMenuBinderBudget.listSelection = todayWeek // 이번주에 해당되는 position으로 세팅
            }

            dropdownMenuBinderBudget.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedWeek = parent.getItemAtPosition(position) as String
            }
        }
    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collect { action ->
                        when (action) {
                            is BinderBudgetAction.ShowToast -> showToast(action.content)
                            is BinderBudgetAction.NavigateWeekBudgetView -> {}
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
                        setDropDownMenu(state)
                    }
                }
            }
        }
    }

    // 드롭박스 메뉴 세팅
    private fun setDropDownMenu(state: BinderBudgetState) {
        var thisWeek = ""
        
        var weekOffset = 0
        
        if (state.step == 1) weekOffset = 1
        else if (state.step == 2) weekOffset = 2
        else if (state.step > 2) weekOffset = 20

        dropdownMenu.addAll(DateUtil.getWeekRange(weekOffset))

        thisWeek = dropdownMenu.last()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            dropdownMenu
        )

        binding.dropdownMenuBinderBudget.apply {
            setAdapter(adapter)
            setText(thisWeek, false)
        }
    }
}