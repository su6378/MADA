package com.example.mada.dialog

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
import kotlin.system.exitProcess

class AlertDialog(
    context: Context,
    private var onCustomListener: (() -> Unit)? = null,
) : Dialog(context) {

    private val binding: DialogAlertBinding = DialogAlertBinding.inflate(layoutInflater)
    private val mainActivity: MainActivity

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.run {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 50))
            attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        } ?: exitProcess(0)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            btnDialogOk.setOnClickListener {
                onCustomListener?.invoke()
                dismiss()
            }
            btnDialogCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun show() {
        if (!this.isShowing) super.show()
    }
}