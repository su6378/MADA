package com.example.mada.feature.on_boarding

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentOnBoardingBinding
import com.nitish.typewriterview.TypeWriterView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "DX"

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding, OnBoardingViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_on_boarding
    override val viewModel: OnBoardingViewModel by viewModels()

    override fun initView() {
        with(binding) {

        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {

        }

    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.action.collect { action ->
                        when (action) {
                            is OnBoardingAction.ShowToast -> showToast(action.content)
                            is OnBoardingAction.NavigateHomeView -> navigate(
                                OnBoardingFragmentDirections.actionOnBoardingFragmentToHomeFragment()
                            )

                            is OnBoardingAction.NavigateAccountLinkView -> navigate(
                                OnBoardingFragmentDirections.actionOnBoardingFragmentToAccountLinkFragment()
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
                    viewModel.state.collectLatest { state ->
                        binding.apply {
                            when (state.step) {
                                1 -> {
                                    delay(1000)
                                    tvOnBoardingGuideComment.animateText(resources.getString(R.string.on_boarding_step_one))
                                    setNextButtonEnable()

                                }

                                2 -> {
                                    tvOnBoardingGuideComment.stopAnimation()
                                    tvOnBoardingGuideComment.animateText(resources.getString(R.string.on_boarding_step_two))
                                    setNextButtonEnable()
                                }

                                3 -> {
                                    tvOnBoardingGuideComment.stopAnimation()
                                    tvOnBoardingGuideComment.animateText(resources.getString(R.string.on_boarding_step_three))
                                    setNextButtonEnable()
                                }

                                4 -> {
                                    tvOnBoardingGuideComment.stopAnimation()
                                    ivOnBoardingCard.startAnimation(
                                        android.view.animation.AnimationUtils.loadAnimation(
                                            requireContext(),
                                            R.anim.fade_in
                                        )
                                    )
                                    ivOnBoardingCard.visibility = View.VISIBLE

                                    ivOnBoardingAlle.startAnimation(
                                        android.view.animation.AnimationUtils.loadAnimation(
                                            requireContext(),
                                            R.anim.fade_out
                                        )
                                    )
                                    ivOnBoardingAlle.visibility = View.INVISIBLE

                                    tvOnBoardingGuideComment.animateText(resources.getString(R.string.on_boarding_step_four))
                                    setNextButtonEnable()
                                }

                                5 -> {
                                    tvOnBoardingGuideComment.stopAnimation()
                                    changeImageWithFade(R.drawable.image_on_boarding_alarm)
                                    tvOnBoardingGuideComment.animateText(resources.getString(R.string.on_boarding_step_five))
                                    setNextButtonEnable()
                                }

                                6 -> {
                                    tvOnBoardingGuideComment.stopAnimation()
                                    changeImageWithFade(R.drawable.image_on_boarding_save)
                                    tvOnBoardingGuideComment.animateText(resources.getString(R.string.on_boarding_step_six))
                                    setNextButtonEnable()
                                }

                                7 -> {
                                    tvOnBoardingGuideComment.stopAnimation()
                                    changeImageWithFade(R.drawable.image_on_boarding_reward)
                                    tvOnBoardingGuideComment.animateText(resources.getString(R.string.on_boarding_step_seven))
                                    setNextButtonEnable()
                                }
                                else -> {
                                    tvOnBoardingGuideComment.stopAnimation()
                                    ivOnBoardingAlle.startAnimation(
                                        android.view.animation.AnimationUtils.loadAnimation(
                                            requireContext(),
                                            R.anim.fade_in
                                        )
                                    )
                                    ivOnBoardingAlle.visibility = View.VISIBLE

                                    ivOnBoardingCard.startAnimation(
                                        android.view.animation.AnimationUtils.loadAnimation(
                                            requireContext(),
                                            R.anim.fade_out
                                        )
                                    )
                                    ivOnBoardingCard.visibility = View.INVISIBLE

                                    tvOnBoardingGuideComment.animateText(resources.getString(R.string.on_boarding_step_eight))
                                    setNextButtonEnable()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 애니메이션 동작시 버튼 비활성화
    private fun setNextButtonEnable() {
        binding.apply {
            btnNavigateAccount.isEnabled = false
            btnNavigateAccount.isClickable = false

            tvOnBoardingGuideComment.setOnAnimationChangeListener {
                btnNavigateAccount.isEnabled = true
                btnNavigateAccount.isClickable = true

                if (viewModel.state.value.step >= 8) {
                    tvOnBoardingInitial.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.fade_in
                        )
                    )
                    tvOnBoardingInitial.visibility = View.VISIBLE


                    lineOnBoarding.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.fade_in
                        )
                    )
                    lineOnBoarding.visibility = View.VISIBLE
                }
            }
        }
    }

    // 이미지 변경시 애니메이션 효과
    private  // 이미지 변경 함수
    fun changeImageWithFade(newImageRes: Int, duration: Long = 500) {
        binding.ivOnBoardingCard.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                // 2️⃣ 이미지 교체
                binding.ivOnBoardingCard.setImageResource(newImageRes)

                // 3️⃣ fade in
                binding.ivOnBoardingCard.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .start()
            }
            .start()
    }
}