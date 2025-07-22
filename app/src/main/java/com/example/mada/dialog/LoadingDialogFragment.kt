package com.example.mada.dialog

import com.example.mada.R
import com.example.mada.base.BaseDialogFragment
import com.example.mada.databinding.DialogLoadingBinding

class LoadingDialogFragment : BaseDialogFragment<DialogLoadingBinding>(){
    override val layoutResourceId: Int
        get() = R.layout.dialog_loading

    override fun initDataBinding() {}
    override fun initView() {}

}