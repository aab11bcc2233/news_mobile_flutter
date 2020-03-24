package com.onesfish.news_mobile

import android.os.Bundle
import android.view.ViewTreeObserver
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.onesfish.news_mobile.model.News
import com.onesfish.news_mobile.model.NewsDetails
import com.onesfish.news_mobile.utils.MediaCache
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import kotlin.concurrent.thread

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)

        flutterView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                flutterView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }

        })

        val methodChannel = MethodChannel(flutterView, CHANNEL)
        methodChannel.setMethodCallHandler { methodCall, result ->
            thread {
                when (methodCall.method) {
                    "startNewsDetails" -> {
                        val map = methodCall.arguments as Map<String, Any>

                        if (map == null) {
                            error(result, "ARGUMENTS", "argument can not be null", null)
                            return@thread
                        }

                        val newsJson = map[NewsDetailsActivity.KEY_NEWS] as String
                        if (newsJson.isNullOrEmpty()) {
                            error(result, "ARGUMENTS", "argument can not be null", null)
                            return@thread
                        }

                        val newsDetailsJson = map[NewsDetailsActivity.KEY_NEWS_DETAILS] as String
                        if (newsDetailsJson.isNullOrEmpty()) {
                            error(result, "ARGUMENTS", "argument can not be null", null)
                            return@thread
                        }

                        try {
                            val gson = Gson()
                            val news = gson.fromJson<News>(newsJson, News::class.java)
                            val newsDetails = gson.fromJson<NewsDetails>(newsDetailsJson, NewsDetails::class.java)

                            NewsDetailsActivity.start(
                                    this@MainActivity,
                                    map[NewsDetailsActivity.KEY_IS_DARK] as Boolean,
                                    news,
                                    newsDetails
                            )
                            success(result, "startNewsDetails Success")
                        } catch (e: Exception) {
                            error(result, "ARGUMENTS", "argument error", null)
                        }
                    }
                    "getCacheSize" -> {
                        val imgCacheSize = try {
                            Glide.getPhotoCacheDir(this)?.walk()?.map { it.length() }?.sum() ?: 0L
                        } catch (e: Exception) {
                            0L
                        }

                        val voiceCacheSize = try {
//                            MediaCache.getSimpleCache(this).cacheSpace
                            MediaCache.getCacheDir(this).walk().map { f -> f.length() }.sum()
                        } catch (e: Exception) {
                            0L
                        }

                        success(
                                result,
                                mapOf<String, Long>(
                                        "img_cache_size" to imgCacheSize,
                                        "voice_cache_size" to voiceCacheSize
                                )
                        )

                    }
                    "clearImageCache" -> {
                        Glide.get(this).clearDiskCache()
                        success(result,true)
                    }
                    "clearVoiceCache" -> {
                        val b = MediaCache.getCacheDir(this).deleteRecursively()
                        success(result,b)
                    }
                    else -> {
                        notImplemented(result)
                    }
                }
            }
        }

    }
    
    private fun error(result: MethodChannel.Result, errorCode: String, errorMessage: String, errorDetails: Any?) {
        runOnUiThread {
            result.error(errorCode, errorMessage, errorDetails)
        }
    }

    private fun success(result: MethodChannel.Result, any: Any?) {
        runOnUiThread {
            result.success(any)
        }
    }

    private fun notImplemented(result: MethodChannel.Result) {
        runOnUiThread {
            result.notImplemented()
        }
    }

    companion object {
        const val CHANNEL = "com.onesfish.news_mobile/method_call"
    }
}
