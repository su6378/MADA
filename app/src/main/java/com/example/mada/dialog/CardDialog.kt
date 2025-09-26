package com.example.mada.dialog

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.MainActivity
import com.example.mada.databinding.DialogCardBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess
import androidx.core.graphics.drawable.toDrawable

private const val TAG = "DX"
class CardDialog(
    context: Context,
) : Dialog(context) {

    private val binding: DialogCardBinding = DialogCardBinding.inflate(layoutInflater)
    private val mainActivity: MainActivity

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.run {
            setBackgroundDrawable(InsetDrawable(Color.TRANSPARENT.toDrawable(), 50))
            setDimAmount(1.0f)
            attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        } ?: exitProcess(0)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            lvLoadingCard.playAnimation()

            lvLoadingCard.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // 애니메이션이 시작될 때
                }

                override fun onAnimationEnd(animation: Animator) {
                    // 애니메이션이 종료될 때
                    this@CardDialog.dismiss()
                }

                override fun onAnimationCancel(animation: Animator) {
                    // 애니메이션이 취소될 때
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // 애니메이션이 시작된 이후 반복될 때
                }
            })
        }
    }

    override fun show() {
        if (!this.isShowing) super.show()
    }
}