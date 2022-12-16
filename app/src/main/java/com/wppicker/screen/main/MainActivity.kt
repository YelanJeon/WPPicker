package com.wppicker.screen.main

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wppicker.R
import com.wppicker.data.TopicData
import com.wppicker.databinding.ActivityMainBinding
import com.wppicker.common.Utils
import com.wppicker.data.PhotoData
import com.wppicker.screen.detail.DetailDialog
import com.wppicker.screen.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    private val viewModel: MainViewModel by viewModels()

    private val topicAdapter: TopicAdapter by lazy { TopicAdapter() }
    private val photoAdapter: PhotoListAdapter by lazy { PhotoListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setDrawer()
        setTopicView()
        setListView()
        setEmptyView()
        setOnClickListeners()

        viewModel.loadTopicList()
        viewModel.loadPhotoList(TopicData.TOPIC_IDX_ALL)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topicFlow.collectLatest {
                    topicAdapter.submitData(it)
                }
            }
        }

        viewModel.selectedTopicPosition.observe(this) {
            selectedPosition ->
                topicAdapter.select(selectedPosition)
                viewModel.loadPhotoList(topicAdapter.getSelectedItem().idx)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photoFlow.collectLatest {
                    val isFirst = photoAdapter.itemCount == 0
                    photoAdapter.submitData(it)
                    if(isFirst){
                        checkEmpty()
                    }
                }
            }
        }
    }

    private fun setDrawer() {
        val span1 = SpannableStringBuilder("Develop by Yelan Chun")
        span1.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.primary, null)), 11, 21, 0)
        val span2 = SpannableStringBuilder("Image From Unsplash")
        span2.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.primary, null)), 11, 19, 0)

        binding.drawer.tvDrawerDeveloper.text = span1
        binding.drawer.tvDrawerUnsplash.text = span2
    }

    private fun setTopicView() {
        binding.rcvMainTopic.adapter = topicAdapter.apply {
            topicClickListener = object : OnTopicClickListener {
                override fun onTopicClicked(topicData: TopicData) {
                    viewModel.setSelectedTopicPosition(topicAdapter.selectedPosition)
                }
            }
        }
        binding.rcvMainTopic.layoutManager = LinearLayoutManager(baseContext, RecyclerView.HORIZONTAL, false)
        binding.rcvMainTopic.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if(parent.getChildAdapterPosition(view) > 0) {
                    outRect.left = Utils.dimensionToPixel(baseContext, 8f).toInt()
                }

            }
        })

    }

    private fun setListView() {
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rcvMainList.layoutManager = layoutManager
        binding.rcvMainList.adapter = photoAdapter
        binding.rcvMainList.addItemDecoration(object: RecyclerView.ItemDecoration() {
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

    private fun setEmptyView() {
        binding.empty.tvEmptyMessage.text = "Failed to load Photos!"
        binding.empty.btnEmptyAction.setOnClickListener {
            photoAdapter.refresh()
        }
    }

    private fun setOnClickListeners() {
        binding.btnMainMenu.setOnClickListener {
            binding.root.openDrawer(GravityCompat.START)
        }

        binding.btnMainSearch.setOnClickListener {
            val keyword = binding.etMainSearch.text.toString()
            if(keyword.isNullOrEmpty()) {
                Toast.makeText(baseContext, "Keyword is empty :(", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(baseContext, SearchActivity::class.java)
                intent.putExtra("keyword", keyword)
                startActivity(intent)
            }

        }

        binding.btnMainLucky.setOnClickListener {
            viewModel.getRandomPhoto(topicAdapter.getSelectedItem().idx) {
                photoIdx ->
                    if(photoIdx.isNullOrEmpty()) {
                        Toast.makeText(baseContext, "Failed to load photo :(", Toast.LENGTH_SHORT).show()
                    }else {
                        DetailDialog.getInstance(photoIdx).show(supportFragmentManager, "random")
                    }
            }
        }
    }

    private fun checkEmpty() {
        if(binding.rcvMainList.adapter!!.itemCount == 0) {
            binding.empty.root.visibility = View.VISIBLE
            binding.btnMainLucky.visibility = View.GONE
        }else{
            binding.empty.root.visibility = View.GONE
            binding.btnMainLucky.visibility = View.VISIBLE
        }
    }
}