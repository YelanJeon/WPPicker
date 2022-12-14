package com.wppicker.screen.search

import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wppicker.common.Utils
import com.wppicker.databinding.ActivitySearchBinding
import com.wppicker.screen.main.PhotoListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity: AppCompatActivity() {

    val viewModel: SearchViewModel by viewModels()

    val binding by lazy { ActivitySearchBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setListView()
        setEmptyView()
        setOnClickListener()

        val basicKeyword = intent.getStringExtra("keyword")
        basicKeyword?.let {
            binding.etSearch.setText(it)
            viewModel.loadPhotos(it, null)
        }

        viewModel.photos.observe(this) {
            photos ->
                (binding.rcvSearchList.adapter as PhotoListAdapter).submitList(photos)
                checkEmpty()
        }
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


    private fun setOnClickListener() {
        binding.btnSearchBack.setOnClickListener {
            finish()
        }

        binding.btnSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()

            if(keyword.isEmpty()) {
                Toast.makeText(baseContext, "keyword is empty", Toast.LENGTH_SHORT).show()
            }else {
                viewModel.loadPhotos(
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
                    viewModel.loadPhotos(binding.etSearch.text.toString(), menus[which])
                }
            })
            builder.create().show()
        }

        binding.tvSearchOrientation.setOnClickListener(onOrientationClickListener)
        binding.ivSearchOrientationIcon.setOnClickListener(onOrientationClickListener)
    }

    private fun setEmptyView() {
        binding.empty.tvEmptyMessage.text = "Failed to load Photos!"
        binding.empty.btnEmptyAction.setOnClickListener { viewModel.loadPhotos(binding.etSearch.text.toString(), binding.tvSearchOrientation.text.toString()) }
    }

    fun checkEmpty() {
        if(binding.rcvSearchList.adapter!!.itemCount == 0) {
            binding.empty.root.visibility = View.VISIBLE
        }else{
            binding.empty.root.visibility = View.GONE
        }
    }
}
