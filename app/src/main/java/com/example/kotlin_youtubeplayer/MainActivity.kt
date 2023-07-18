package com.example.kotlin_youtubeplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_youtubeplayer.adapter.VideoCaptionAdapter
import com.example.kotlin_youtubeplayer.data.VideoDetailReq
import com.example.kotlin_youtubeplayer.data.VideoDetailRes
import com.example.kotlin_youtubeplayer.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val tag = "MainActivity"
    private val captionList = ArrayList<VideoDetailRes.Captions>()

    private var videoUrl = ""
    private lateinit var captionAdapter: VideoCaptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCaptionList()

        CoroutineScope(Dispatchers.IO).launch {
            getVideoAPI()
        }
    }

    private fun initCaptionList() {
        if (!::captionAdapter.isInitialized)
            captionAdapter = VideoCaptionAdapter(captionList)

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = captionAdapter
        }
    }


    private fun getVideoAPI() {
        val req = VideoDetailReq("44f6cfed-b251-4952-b6ab-34de1a599ae4", "5edfb3b04486bc1b20c2851a", 1)
        val parameter = Gson().toJson(req)
        val apiUrl = "https://api.italkutalk.com/api/video/detail"
        val reqBuilder = Request.Builder().url(apiUrl)
            .post(parameter.toRequestBody("application/json; charset=utf-8".toMediaType()))
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        client.newCall(reqBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(tag, "API onFailure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonString = response.body.string()
                    val res = Gson().fromJson(jsonString, VideoDetailRes::class.java)

                    captionList.clear()
                    captionList.addAll(res.result.videoInfo.captionResult.results[0].captions)
                    CoroutineScope(Dispatchers.Main).launch {
                        captionAdapter.notifyItemRangeInserted(0, captionList.size)
                    }

                } catch (e: Exception) {
                    Log.e(tag, "$e")
                }
            }
        })
    }
}