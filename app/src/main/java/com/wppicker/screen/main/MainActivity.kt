package com.wppicker.screen.main

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wppicker.R
import com.wppicker.common.MyRetrofit
import com.wppicker.data.TopicData
import com.wppicker.databinding.ActivityMainBinding
import com.wppicker.common.Utils
import com.wppicker.data.PhotoData
import com.wppicker.request.ReqImage
import com.wppicker.screen.detail.DetailDialog
import com.wppicker.screen.search.SearchActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity: AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    val viewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(MainRepository(MyRetrofit.retrofit)))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setDrawer()
        setTopicView()
        setListView()
        setEmptyView()
        setOnClickListeners()

        viewModel.loadTopics()

        viewModel.topics.observe(this) {
            topicList ->
                (binding.rcvMainTopic.adapter as TopicAdapter).submitList(topicList)
                viewModel.loadPhotos((binding.rcvMainTopic.adapter as TopicAdapter).getSelectedItem().idx)
        }

        viewModel.photos.observe(this) {
            photoList ->
                (binding.rcvMainList.adapter as PhotoListAdapter).submitList(photoList)
                checkEmpty()
        }

        viewModel.randomPhotoIdx.observe(this) {
            photoIdx ->
                if(photoIdx.isNullOrEmpty()) {
                    Toast.makeText(baseContext, "Failed to load photo :(", Toast.LENGTH_SHORT).show()
                }else {
                    DetailDialog.getInstance(photoIdx).show(supportFragmentManager, "random")
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
        val adapter = TopicAdapter()
        binding.rcvMainTopic.adapter = adapter.apply {
            topicClickListener = object : OnTopicClickListener {
                override fun onTopicClicked(topicData: TopicData) {
                    binding.rcvMainTopic.scrollToPosition((binding.rcvMainTopic.adapter as TopicAdapter).selectedPosition)
                    viewModel.loadPhotos(topicData.idx)
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
        binding.rcvMainList.adapter = PhotoListAdapter()
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
            viewModel.loadPhotos((binding.rcvMainTopic.adapter as TopicAdapter).getSelectedItem().idx)
        }
    }

    private fun setOnClickListeners() {
        binding.btnMainMenu.setOnClickListener {
            binding.root.openDrawer(GravityCompat.START)
        }

        binding.btnMainSearch.setOnClickListener {
            val keyword = binding.etMainSearch.text.toString()

            if(keyword.isEmpty()) {
                Toast.makeText(baseContext, "keyword is empty", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(MainActivity@this, SearchActivity::class.java)
                intent.putExtra("keyword", keyword)
                startActivity(intent)
                binding.etMainSearch.text = null
            }
        }

        binding.btnMainLucky.setOnClickListener {
            viewModel.getRandomPhoto((binding.rcvMainTopic.adapter as TopicAdapter).getSelectedItem().idx);
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
