package com.onesfish.news_mobile.utils

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.onesfish.news_mobile.R
import com.onesfish.news_mobile.utils.MediaCache.Companion.VOICE_MAX_CACHE_SIZE
import java.io.File

/**
 *
 * Create by htp on 2019/9/5
 */

class MyCacheDataSourceFactory(private val context: Context, private val maxCacheSize: Long = VOICE_MAX_CACHE_SIZE, private val maxFileSize: Long = VOICE_MAX_FILE_SIZE) : DataSource.Factory {

    private val defaultDatasourceFactory: DefaultDataSourceFactory

    init {
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        defaultDatasourceFactory = DefaultDataSourceFactory(this.context,
                bandwidthMeter,
                DefaultHttpDataSourceFactory(userAgent, bandwidthMeter))
    }

    override fun createDataSource(): DataSource {
        val simpleCache = MediaCache.getSimpleCache(context, maxCacheSize)
        return CacheDataSource(
                simpleCache,
                defaultDatasourceFactory.createDataSource(),
                FileDataSource(),
                CacheDataSink(simpleCache, maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                null)
    }

    companion object {
        const val VOICE_MAX_FILE_SIZE = 1024L * 1024L * 10L
    }

}

class MediaCache private constructor() {
    companion object {
        const val VOICE_MAX_CACHE_SIZE = 1024L * 1024L * 1024L
        private var simpleCache: SimpleCache? = null

        fun getSimpleCache(context: Context, maxCacheSize: Long = VOICE_MAX_CACHE_SIZE): SimpleCache {
            if (simpleCache == null) {
                simpleCache = SimpleCache(
                        getCacheDir(context),
                        LeastRecentlyUsedCacheEvictor(maxCacheSize),
                        ExoDatabaseProvider(context))
            }
            return simpleCache!!
        }

        fun getCacheDir(context: Context) = File(context.cacheDir, "media_voice")
    }
}