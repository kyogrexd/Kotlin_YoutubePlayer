package com.example.kotlin_youtubeplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
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
import okhttp3.MediaType.Companion.get
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

        setWebView()

        binding.clPlay.setOnClickListener {
            pauseVideo()
        }

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

        captionAdapter.apply {
            onItemClick = { position, second ->
                //滾動
                (binding.rvList.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(position, 0)

                //影片播放位置
                seekTo(second)

                //換色
                setCaptionBackground(position)
            }
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

    private fun setWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            //自動適應螢幕大小
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            //將圖片調整到適合WebView的大小
            settings.useWideViewPort = true
            webViewClient = WebViewClient()
            addJavascriptInterface(JsInterface(), "android")
        }

        val htmlData = getHTMLData("9nhhQhAxhjo")
        binding.webView.loadDataWithBaseURL("https://www.youtube.com", htmlData, "text/html", "UTF-8", null)
    }

    private fun getHTMLData(videoId: String): String {
        return """
            <!DOCTYPE html>
            <html>
                <style type="text/css">
                body {
                    top: 0%;
                    left: 0%;
                    bottom: 0%;
                    margin: 0;
                    height: 100%;
                    width: 100%;
                    background-color: #000000;
                    overflow: hidden;
                    position: absolute;
                }
                </style>
              <body>
                <!-- 1. The <iframe> (and video player) will replace this <div> tag. -->
                <div id="player"></div>

                <script>
                  // 2. This code loads the IFrame Player API code asynchronously.
                  var tag = document.createElement('script');

                  tag.src = "https://www.youtube.com/iframe_api";
                  var firstScriptTag = document.getElementsByTagName('script')[0];
                  firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                  // 3. This function creates an <iframe> (and YouTube player)
                  //    after the API code downloads.
                  var player;
                  function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                      height: '100%',
                      width: '100%',
                      videoId: '$videoId',
                      //https://developers.google.com/youtube/player_parameters?hl=zh-tw
                      playerVars: {
                        'modestbranding': 1,// 隱藏YouTube Logo
                        'enablejsapi': 1,
                        'showinfo': 0,// 隱藏影片標題
                        'fs': 0, //避免播放器的全螢幕按鈕顯示
                        'rel': 0, //播放器就不會顯示相關影片
                        'controls': 1, //顯示播放器的控制項
                        'playsinline': 1,
                        'frameborder': 0,
                        'mute': 0, //靜音
                        'autoplay': 1 //自動播放
                      },
                      events: {
                        'onReady': onPlayerReady,
                        'onStateChange': onPlayerStateChange
                      }
                    });
                  }

                  // 4. The API will call this function when the video player is ready.
                  function onPlayerReady(event) {
                    event.target.playVideo();
                  }

                  // 5. The API calls this function when the player's state changes.
                  //    The function indicates that when playing a video (state=1),
                  //    the player should play for six seconds and then stop.
                  var refreshInterval;
                  function onPlayerStateChange(event) {
                    clearInterval(refreshInterval)

                    switch (player.getPlayerState()) {
                        case YT.PlayerState.UNSTARTED:
                            sendStateChange("-1");
                            return;
                        case YT.PlayerState.ENDED:
                            sendStateChange("0");
                            return;

                        case YT.PlayerState.PLAYING:
                            sendStateChange("1");

                            startSendCurrentTimeInterval();
                            return;

                        case YT.PlayerState.PAUSED:
                            sendStateChange("2");
                            return;

                        case YT.PlayerState.BUFFERING:
                            sendStateChange("3");
                            return;

                        case YT.PlayerState.CUED:
                            sendStateChange("4");
                            return;
                    }
                  }
                  
                  function startSendCurrentTimeInterval() {
                    refreshInterval = setInterval(function() {
                        android.getCurrentTime(player.getCurrentTime());
                    }, 500)
                  }
                  
                  function sendStateChange(newState) {
                    android.getStateChange(newState)
                  }
                  
                  function seekTo(time) {
                    player.seekTo(time, true);
                  }
                  
                  function playVideo() {
                    player.playVideo();
                  }
                  
                  function pauseVideo() {
                    player.pauseVideo();
                  }
                  
                  function stopVideo() {
                    player.stopVideo();
                  }
                </script>
              </body>
            </html>
        """.trimIndent()
    }

    inner class JsInterface {
        @JavascriptInterface
        fun getStateChange(data: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val status = when (data) {
                    "-1" -> "UNSTARTED"
                    "0" -> "ENDED"
                    "1" -> "PLAYING"
                    "2" -> "PAUSED"
                    "3" -> "BUFFERING"
                    "4" -> "VIDEO_CUED"
                    else -> "UNKNOWN"
                }
                Log.d(tag, "$status")
            }
        }

        @JavascriptInterface
        fun getCurrentTime(time: Int) {
            CoroutineScope(Dispatchers.Main).launch {
                captionList.forEachIndexed { position, captions ->
                    if (captions.miniSecond.toInt() == time) {
                        setCaptionBackground(position)
                        (binding.rvList.layoutManager as LinearLayoutManager)
                            .scrollToPositionWithOffset(position, 0)
                    }
                }
            }
        }
    }

    private fun setCaptionBackground(pos: Int) {
        for (index in 0..binding.rvList.childCount) {
            val v = binding.rvList.getChildAt(index)
            v?.setBackgroundColor(resources.getColor(android.R.color.white, null))
        }

        //指定位置的背景改成灰色
        val childIndex = pos - (binding.rvList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val view = binding.rvList.getChildAt(childIndex)
        view?.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
    }

    private fun seekTo(time: Double) {
        binding.webView.evaluateJavascript("seekTo($time)") {

        }
    }

    private fun playVideo() {
        binding.webView.evaluateJavascript("playVideo()") {

        }
    }

    private fun pauseVideo() {
        binding.webView.evaluateJavascript("") {

        }
    }

    private fun stopVideo() {
        binding.webView.evaluateJavascript("stopVideo()") {

        }
    }
}