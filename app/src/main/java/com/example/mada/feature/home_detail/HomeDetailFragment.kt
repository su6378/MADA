package com.example.mada.feature.home_detail

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mada.R
import com.example.mada.base.BaseFragment
import com.example.mada.databinding.FragmentHomeDetailBinding
import com.example.mada.dialog.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class HomeDetailFragment : BaseFragment<FragmentHomeDetailBinding, HomeDetailViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_home_detail
    override val viewModel: HomeDetailViewModel by viewModels()

    override fun initView() {
        with(binding) {

            val anim = android.view.animation.AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.fade_in
            ).apply {
                duration = 2000L
                interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            }

            btnHomeDetailSave.startAnimation(anim)
            btnHomeDetailSave.visibility = View.VISIBLE

            btnHomeDetailShare.startAnimation(anim)
            btnHomeDetailShare.visibility = View.VISIBLE
        }
    }

    override fun initDataBinding() {
        binding.vm = viewModel

        binding.apply {
            toolbarHomeDetail.setNavigationOnClickListener {
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
                            is HomeDetailAction.ShareHomeImage -> showAlertDialog(
                                dialog = AlertDialog(
                                    mainActivity,
                                    title = resources.getString(R.string.home_share_image),
                                    content = resources.getString(R.string.home_share_image_comment)
                                ) {
                                    shareDrawableImage(requireContext(), action)
                                }, viewLifecycleOwner
                            )

                            is HomeDetailAction.SaveHomeImage -> showAlertDialog(
                                dialog = AlertDialog(
                                    mainActivity,
                                    title = resources.getString(R.string.home_save_image),
                                    content = resources.getString(R.string.home_save_image_comment)
                                ) {
                                    saveDrawableToGallery(requireContext(), R.drawable.image_home_detail,"김모아의 모아하우스")
                                }, viewLifecycleOwner
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
                    viewModel.state.collect { state ->


                    }
                }
            }
        }
    }

    // 집 이미지 저장
    private fun saveDrawableToGallery(context: Context, drawableResId: Int, fileName: String): Uri? {
        val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return null
        val bitmap = (drawable as BitmapDrawable).bitmap

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MADA") // 저장 경로
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }

        showToast(resources.getString(R.string.home_save_image_complete))

        return uri
    }

    // 집 이미지 공유
    private fun shareDrawableImage(context: Context, state: HomeDetailAction) {
        var drawableId: Int = R.drawable.image_home_detail

//        when(state.step) {
//            0 -> drawableId = R.drawable.image_home_initial
//            1 -> drawableId = R.drawable.image_home_one_week
//            2 -> drawableId = R.drawable.image_home_two_week
//            else -> drawableId = R.drawable.image_home_last_week
//        }

        // Drawable → Bitmap 변환
        val drawable = ContextCompat.getDrawable(context, drawableId)!!
        val bitmap = (drawable as BitmapDrawable).bitmap

        // Bitmap → File 변환 (캐시 폴더 저장)
        val file = File(context.cacheDir, "shared_image.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // FileProvider 로 URI 발급
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        // 공유 Intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "이미지 공유"))
    }
}