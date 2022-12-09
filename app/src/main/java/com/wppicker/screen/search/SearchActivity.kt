package com.wppicker.screen.search

import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wppicker.common.MyRetrofit
import com.wppicker.common.Utils
import com.wppicker.data.PhotoData
import com.wppicker.data.SearchPhotoResultData
import com.wppicker.databinding.ActivitySearchBinding
import com.wppicker.request.ReqImage
import com.wppicker.screen.main.PhotoListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity: AppCompatActivity() {

    val binding by lazy { ActivitySearchBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setListView()
        setEmptyView()

        val basicKeyword = intent.getStringExtra("keyword")
        basicKeyword?.let {
            binding.etSearch.setText(it)
            loadPhotos(it, null)
        }

        binding.btnSearchBack.setOnClickListener {
            finish()
        }

        binding.btnSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()

            if(keyword.isEmpty()) {
                Toast.makeText(baseContext, "keyword is empty", Toast.LENGTH_SHORT).show()
            }else {
                loadPhotos(
                    binding.etSearch.text.toString(),
                    binding.tvSearchOrientation.text.toString()
                )
            }
        }

        val onOrientationClickListener = View.OnClickListener() {
            val builder = MaterialAlertDialogBuilder(SearchActivity@this)
            builder.setTitle("Choose orientation")
            val menus = arrayOf("All", "Portrait", "Landscape", "Squarish")
            builder.setItems(menus, object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    binding.tvSearchOrientation.text = menus[which]
                    loadPhotos(binding.etSearch.text.toString(), menus[which])
                }
            })
            builder.create().show()
        }

        binding.tvSearchOrientation.setOnClickListener(onOrientationClickListener)
        binding.ivSearchOrientationIcon.setOnClickListener(onOrientationClickListener)

    }

    private fun setListView() {
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rcvSearchList.layoutManager = layoutManager
        binding.rcvSearchList.adapter = PhotoListAdapter()
        binding.rcvSearchList.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
                val spanIndex = params.spanIndex

                outRect.bottom = Utils.dimensionToPixel(baseContext, 8f).toInt()
                if(spanIndex == 0) {
                    outRect.right = Utils.dimensionToPixel(baseContext, 4f).toInt()
                }else{
                    outRect.left = Utils.dimensionToPixel(baseContext, 4f).toInt()
                }

            }
        })
    }

    private fun loadPhotos(keyword: String, orientation: String?) {
        val mOrientation = if(orientation == "All") null else orientation?.lowercase()

        val callback = object : Callback<SearchPhotoResultData> {
            override fun onResponse(call: Call<SearchPhotoResultData>, response: Response<SearchPhotoResultData>) {
                Log.i("TEST", "response > " + response.code())
                if(response.code() == 200) {
                    val list = response.body()!!.resultList
                    (binding.rcvSearchList.adapter as PhotoListAdapter).submitList(list)
                }
                checkEmpty()
            }

            override fun onFailure(call: Call<SearchPhotoResultData>, t: Throwable) {
                checkEmpty()
                t.printStackTrace()
            }

            fun checkEmpty() {
                if(binding.rcvSearchList.adapter!!.itemCount == 0) {
                    binding.empty.root.visibility = View.VISIBLE
                }else{
                    binding.empty.root.visibility = View.GONE
                }
            }
        }

        MyRetrofit.retrofit.create(ReqImage::class.java).getSearchPhotoList(keyword, mOrientation).enqueue(callback)

    }

    private fun setEmptyView() {
        binding.empty.tvEmptyMessage.text = "Failed to load Photos!"
        binding.empty.btnEmptyAction.setOnClickListener { loadPhotos(binding.etSearch.text.toString(), binding.tvSearchOrientation.text.toString()) }
    }
}
