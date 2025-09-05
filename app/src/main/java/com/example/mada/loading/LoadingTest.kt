package com.example.mada.loading

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.example.mada.MainActivity
import com.example.mada.databinding.DialogAlertBinding
import com.example.mada.databinding.DialogLoadingBinding
import kotlin.system.exitProcess

class LoadingTest(
    context: Context,
    private var onCustomListener: (() -> Unit)? = null,
) : Dialog(context) {

    private val binding: DialogLoadingBinding = DialogLoadingBinding.inflate(layoutInflater)
    private val mainActivity: MainActivity

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.run {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 50))
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
            attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        } ?: exitProcess(0)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
        }
    }

    override fun show() {
        if (!this.isShowing) super.show()
    }
}