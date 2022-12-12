package com.wppicker.screen.detail

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.wppicker.R
import com.wppicker.common.MyRetrofit
import com.wppicker.databinding.DlgDetailBinding
import com.wppicker.common.MyWallpaperManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailDialog() : DialogFragment() {

    private val viewModel by lazy {
        ViewModelProvider(this, DetailViewModel.Factory(DetailRepository(MyRetrofit.retrofit)))[DetailViewModel::class.java]
    }

    private var binding : DlgDetailBinding? = null

    companion object {
        fun getInstance(imageIdx: String): DetailDialog {
            val dialog = DetailDialog();
            val bundle = Bundle()
            bundle.putString("IDX", imageIdx)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DlgDetailBinding.inflate(layoutInflater)

        if(requireArguments().getString("IDX") == null) {
            Toast.makeText(requireContext(), "Cannot found image!", Toast.LENGTH_SHORT).show()
            dismiss()
        }else{
            viewModel.loadPhoto(requireArguments().getString("IDX")!!)
        }

        setClickListener()

        viewModel.intent.observe(this) {
            intent -> startActivity(intent)
        }

        viewModel.photo.observe(this) {
            photo ->
            if(photo.urls.fullImageURL.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Cannot found image!", Toast.LENGTH_SHORT).show()
            }else{
                Log.i("TEST", "URL > " + photo.urls.fullImageURL);
                Glide.with(requireContext()).asBitmap().load(photo.urls.fullImageURL).centerCrop().into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Log.i("TEST", "onResourceReady")
                        CoroutineScope(Dispatchers.Default).launch {
                            val newSize = getNewSize(resource.width, resource.height, getBinding().ivDetailBackground.width, getBinding().ivDetailBackground.height)
                            val newBitmap = Bitmap.createScaledBitmap(resource, newSize[0], newSize[1], true)
                            CoroutineScope(Dispatchers.Main).launch {
                                Log.i("TEST", "bitmapResizing finish")
                                getBinding().ivDetailBackground.setImageBitmap(newBitmap)
                            }
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.i("TEST", "onLoadCleared")
                        placeholder?.let {
                            getBinding().ivDetailBackground.setImageDrawable(placeholder)
                        }
                    }
                })
                getBinding().tvDetailAuthorName.text = photo.author.authorName
                if(photo.author.portfolioURL.isNullOrEmpty()) {
                    getBinding().tvDetailAuthorLink.text = "no portfolio"
                }else{
                    getBinding().tvDetailAuthorLink.text = photo.author.portfolioURL
                }
            }
        }

        return getBinding().root
    }

    private fun getNewSize(resourceWidth: Int, resourceHeight: Int, viewWidth: Int, viewHeight: Int): Array<Int> {
        var newWidth = 0
        var newHeight = 0
        if(resourceWidth > resourceHeight) {
            newHeight = viewHeight
            newWidth = newHeight * resourceWidth / resourceHeight
        }else{
            newWidth = viewWidth
            newHeight = newWidth * resourceHeight / resourceWidth
        }
        return arrayOf(newWidth, newHeight)
    }

    private fun setClickListener() {
        getBinding().btnDetailPick.setOnClickListener {
            copyBitmap()?.let {
                MyWallpaperManager.showSettingDialog(requireContext(), it, "Success")
            }
        }

        getBinding().btnDetailBack.setOnClickListener {
            dismiss()
        }

        getBinding().tvDetailAuthorLink.setOnClickListener {
            val portfolio = getBinding().tvDetailAuthorLink.text
            if(portfolio.startsWith("http")) {
                viewModel.setPortfolio(portfolio.toString())
            }
        }
    }

    private fun copyBitmap(): Bitmap? {
        val width = getBinding().ivDetailBackground.width
        val height = getBinding().ivDetailBackground.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(0xFFEAEAEA.toInt())
        getBinding().ivDetailBackground.draw(canvas)
        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullsizeDialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun getBinding() : DlgDetailBinding {
        return binding!!
    }
}