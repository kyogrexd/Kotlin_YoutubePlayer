package com.example.kotlin_youtubeplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            getVideoAPI()
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
                    Log.d(tag, "${res.result.videoID}")

                } catch (e: Exception) {
                    Log.e(tag, "$e")
                }
            }
        })
    }
}