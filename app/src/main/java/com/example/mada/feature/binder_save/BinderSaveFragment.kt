package com.example.mada.feature.binder_save

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.R
import com.example.mada.adapter.SaveAdapter
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentBinderSaveBinding
import com.example.mada.util.DecorationUtil
import com.example.mada.util.ImageUtil.changeImageWithFade
import com.example.mada.util.TextUtil.setColoredSubstrings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class BinderSaveFragment : BaseFragment<FragmentBinderSaveBinding, BinderSaveViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_binder_save
    override val viewModel: BinderSaveViewModel by viewModels()

    private lateinit var saveAdapter: SaveAdapter

    override fun initView() {
        with(binding) {
            tvBinderSaveProgress.setColoredSubstrings(
                viewModel.state.value.budgetProgressText,
                listOf("${viewModel.state.value.budgetProgress}%"),
                R.color.nh_green
            )

            val spacingInPx = resources.getDimensionPixelSize(R.dimen.spacing_20dp)

            with(rvBinderSave) {
                itemAnimator = null
                adapter = saveAdapter
            }
        }
    }

    override fun initDataBinding() {
        binding.apply {
            vm = viewModel
            saveAdapter = SaveAdapter()
            toolbarBinderSave.setNavigationOnClickListener {
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
                            is BinderSaveAction.ShowToast -> showToast(action.content)
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
                        binding.apply {
                            when (state.saveBinderImage) {
                                "cloud" -> ivBinderSaveDiary.changeImageWithFade(R.drawable.binder_cloud)
                                "heart" -> ivBinderSaveDiary.changeImageWithFade(R.drawable.binder_heart)
                                "green" -> ivBinderSaveDiary.changeImageWithFade(R.drawable.binder_green)
                                "alle" -> ivBinderSaveDiary.changeImageWithFade(R.drawable.binder_alle)
                                "onee" -> ivBinderSaveDiary.changeImageWithFade(R.drawable.binder_onee)
                                "ribbon" -> ivBinderSaveDiary.changeImageWithFade(R.drawable.binder_ribbon)
                            }
                        }
                    }
                }
            }
        }
    }
}