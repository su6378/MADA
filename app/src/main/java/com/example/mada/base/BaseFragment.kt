package com.example.mada.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import com.example.mada.MainActivity
import com.example.mada.loading.LoadingTest

abstract class BaseFragment<T : ViewDataBinding, R : ViewModel> : Fragment() {

    // 사용하고자 하는 layoutId
    abstract val layoutResourceId: Int

    // Fragment에 사용되는 viewModel
    abstract val viewModel: R

    private var _binding: T? = null
    val binding get() = requireNotNull(_binding)

//    private val loadingDialog by lazy { LoadingTest() }

    /**
     * View에 필요한 객체(adpater..)를 설정
     * */
    abstract fun initView()

    /**
     * Databinding에 필요한 값을 설정
     * */
    abstract fun initDataBinding()

    /**
     * viewModel에서 사용되는 값을 Observing
     * */
    abstract fun initObserving()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        initDataBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserving()
    }

    // alert 다이얼로그 생성
    protected fun showAlertDialog(
        dialog: Dialog,
        lifecycleOwner: LifecycleOwner?,
        cancelable: Boolean = true,
        dismissHandler: (() -> Unit)? = null,
    ) {
        val targetEvent = if (cancelable) Lifecycle.Event.ON_STOP else Lifecycle.Event.ON_DESTROY
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == targetEvent && dialog.isShowing) {
                dialog.dismiss()
                dismissHandler?.invoke()
            }
        }
        dialog.show()
        lifecycleOwner?.lifecycle?.addObserver(observer)
        dialog.setOnDismissListener { lifecycleOwner?.lifecycle?.removeObserver(observer) }
    }

    protected fun showLoading(
        dialog: Dialog,
    ) {
        dialog.show()
    }

    protected fun showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    /**
     * feature 모듈 간 실행해야함
     * */
    protected fun navigate(direction: NavDirections) {
        val controller = findNavController(binding.root)
        // TODO 예외처리 구현 여부 확인
        controller.navigate(direction)
    }

    protected fun backNavigate() {
        val controller = findNavController(binding.root)

        controller.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}