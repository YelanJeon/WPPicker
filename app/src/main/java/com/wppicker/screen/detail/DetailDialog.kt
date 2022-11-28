package com.wppicker.screen.detail

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.wppicker.R
import com.wppicker.common.MyRetrofit
import com.wppicker.databinding.DlgDetailBinding
import com.wppicker.common.MyWallpaperManager
import com.wppicker.common.Utils
import com.wppicker.data.PhotoData
import com.wppicker.request.ReqImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailDialog() : DialogFragment() {
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
            Toast.makeText(requireContext(), "Image not found!", Toast.LENGTH_SHORT).show()
            dismiss()
        }else{
            MyRetrofit.retrofit.create(ReqImage::class.java).getImageDetail(requireArguments().getString("IDX")!!).enqueue(object : Callback<PhotoData> {
                override fun onResponse(call: Call<PhotoData>, response: Response<PhotoData>) {
                    if(response.code() == 200) {
                        val data = response.body()!!
                        val imgUrl = data.urls.fullImageURL
                        Glide.with(requireContext()).asBitmap().load(imgUrl).centerCrop().into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val newSize = getNewSize(resource.width, resource.height, getBinding().ivDetailBackground.width, getBinding().ivDetailBackground.height)
                                    val newBitmap = Bitmap.createScaledBitmap(resource, newSize[0], newSize[1], true)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        getBinding().ivDetailBackground.setImageBitmap(newBitmap)
                                    }
                                }

                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                placeholder!!.let {
                                    getBinding().ivDetailBackground.setImageDrawable(placeholder)
                                }
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
                        })
                        getBinding().tvDetailAuthorName.text = data.author.authorName
                        if(data.author.portfolioURL.isNullOrEmpty()) {
                            getBinding().tvDetailAuthorLink.text = "no portfolio"
                        }else{
                            getBinding().tvDetailAuthorLink.text = data.author.portfolioURL
                        }

                    }else{
                        Toast.makeText(requireContext(), "Image not found!", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }

                override fun onFailure(call: Call<PhotoData>, t: Throwable) {
                    Toast.makeText(requireContext(), "Image not found!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            })
        }

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
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(portfolio.toString())
                startActivity(intent)
            }

        }

        return getBinding().root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullsizeDialog)
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun getBinding() : DlgDetailBinding {
        return binding!!
    }
}