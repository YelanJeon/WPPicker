package com.wppicker.screen.main

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wppicker.R
import com.wppicker.common.MyRetrofit
import com.wppicker.data.TopicData
import com.wppicker.databinding.ActivityMainBinding
import com.wppicker.common.Utils
import com.wppicker.data.PhotoData
import com.wppicker.data.TopicPhotoURLS
import com.wppicker.data.TopicPhotoURL
import com.wppicker.request.ReqImage
import com.wppicker.screen.detail.DetailDialog
import com.wppicker.screen.search.SearchActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity: AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setDrawer()
        setTopicView()
        setListView()
        setEmptyView()

        loadTopics()

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
            showRandomPhotoDialog()
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
                    loadPhotos(topicData.idx)
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

    private fun loadTopics() {
        var defaultList = listOf(TopicData(TopicData.TOPIC_IDX_ALL, "All Topics", TopicPhotoURLS(TopicPhotoURL(""))))

        MyRetrofit.retrofit.create(ReqImage::class.java).getTopicList().enqueue(object:Callback<List<TopicData>> {
            override fun onResponse(call: Call<List<TopicData>>, response: Response<List<TopicData>>) {
                var list = defaultList
                if(response.code() == 200) {
                    list = list + response.body()!!
                }
                setAdapter(list)
                loadPhotos(TopicData.TOPIC_IDX_ALL)
            }

            override fun onFailure(call: Call<List<TopicData>>, t: Throwable) {
                setAdapter(defaultList)
                loadPhotos(TopicData.TOPIC_IDX_ALL)
            }

            private fun setAdapter(list: List<TopicData>) {
                (binding.rcvMainTopic.adapter as TopicAdapter).dataList = list
                (binding.rcvMainTopic.adapter as TopicAdapter).notifyDataSetChanged()
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

    private fun loadPhotos(topicIdx: String) {
        val callback = object : Callback<List<PhotoData>> {
            override fun onResponse(call: Call<List<PhotoData>>, response: Response<List<PhotoData>>) {
                if(response.code() == 200) {
                    val list = response.body()!!
                    (binding.rcvMainList.adapter as PhotoListAdapter).dataList = list
                    (binding.rcvMainList.adapter as PhotoListAdapter).notifyDataSetChanged()
                }
                checkEmpty()
            }

            override fun onFailure(call: Call<List<PhotoData>>, t: Throwable) {
                checkEmpty()
            }

            fun checkEmpty() {
                if(binding.rcvMainList.adapter!!.itemCount == 0) {
                    binding.empty.root.visibility = View.VISIBLE
                    binding.btnMainLucky.visibility = View.GONE
                }else{
                    binding.empty.root.visibility = View.GONE
                    binding.btnMainLucky.visibility = View.VISIBLE
                }
            }
        }

        if(topicIdx == TopicData.TOPIC_IDX_ALL) {
            MyRetrofit.retrofit.create(ReqImage::class.java).getAllImageList().enqueue(callback)
        }else{
            MyRetrofit.retrofit.create(ReqImage::class.java).getImageList(topicIdx).enqueue(callback)
        }
    }

    private fun setEmptyView() {
        binding.empty.tvEmptyMessage.text = "Failed to load Photos!"
        binding.empty.btnEmptyAction.setOnClickListener { loadPhotos((binding.rcvMainTopic.adapter as TopicAdapter).getSelectedItem().idx) }
    }

    private fun showRandomPhotoDialog() {
        MyRetrofit.retrofit.create(ReqImage::class.java).getRandomPhoto().enqueue(object : Callback<PhotoData> {
            override fun onResponse(call: Call<PhotoData>, response: Response<PhotoData>) {
                if(response.code() == 200) {
                    DetailDialog.getInstance(response.body()!!.photoIdx).show(supportFragmentManager, "random")
                }else{
                    Toast.makeText(baseContext, "Failed to load Image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PhotoData>, t: Throwable) {
                Toast.makeText(baseContext, "Failed to load Image", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
