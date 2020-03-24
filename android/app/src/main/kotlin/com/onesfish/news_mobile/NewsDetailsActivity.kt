package com.onesfish.news_mobile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.source.DefaultMediaSourceEventListener
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.video.VideoRendererEventListener
import com.google.android.material.appbar.AppBarLayout
import com.htphtp.tools.StatusBarUtils
import com.htphtp.tools.getDimensionPixelSize
import com.onesfish.news_mobile.model.News
import com.onesfish.news_mobile.model.NewsDetails
import com.onesfish.news_mobile.utils.AppBarStateChangeListener
import com.onesfish.news_mobile.utils.MyCacheDataSourceFactory
import kotlinx.android.synthetic.main.activity_news_details.*
import java.io.IOException
import kotlin.properties.Delegates

/**
 *
 * Create by htp on 2019/8/19
 */
class NewsDetailsActivity : AppCompatActivity(), Player.EventListener {
    private val TAG = javaClass.simpleName

    private var isDark: Boolean by Delegates.observable(false, { _, _, newValue ->
        if (newValue) {
            rootView.setBackgroundColor(Color.parseColor("#FF424242"))
            bottomToolbar.setCardBackgroundColor(Color.parseColor("#FF424242"))
            floatingActionButton.supportBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF424242"))
            tvDate.setTextColor(Color.parseColor("#A6B0C9"))
            tvTitle.setTextColor(Color.parseColor("#FFBDBDBD"))

            iconHiraganaBg.setBackgroundResource(R.drawable.ripple_less_grey)
            iconBack.setBackgroundResource(R.drawable.ripple_less_grey)

            window.navigationBarColor = Color.parseColor("#FF424242")

        } else {
            rootView.setBackgroundColor(Color.parseColor("#FFF2E2"))
            bottomToolbar.setCardBackgroundColor(Color.parseColor("#FFF2E2"))

            iconHiraganaBg.setBackgroundResource(R.drawable.ripple_less)
            iconBack.setBackgroundResource(R.drawable.ripple_less)

            window.navigationBarColor = Color.parseColor("#FFF2E2")
        }
    })
    private var news: News? = null
    private var newsDetails: NewsDetails? = null

    private var levelHiragana = ICON_LEVEL_HIRAGANA_A_STROKE

    private val tvTitleShowAnimator: ObjectAnimator by lazy { ObjectAnimator.ofFloat(tvTitle, "alpha", 0f, 1f) }
    private val tvTitleHideAnimator: ObjectAnimator by lazy { ObjectAnimator.ofFloat(tvTitle, "alpha", 1f, 0f) }

    private val player: SimpleExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this, AudioOnlyRenderersFactory(this), DefaultTrackSelector()).apply {
            playWhenReady = true
            addListener(this@NewsDetailsActivity)
        }
    }
    private var mediaSource: HlsMediaSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)
        handleIntent(intent)
        initViews()
        initClicks()

        showData()
    }

    private fun initViews() {
        setStatusBar()
        initWebView()
        setBtnHiraganaState()

        tvTitle.alpha = 0f
        progressCircular.alpha = 0f
        webView.alpha = 0f

        fabParent.translationY = (getDimensionPixelSize(R.dimen.news_details_fab_bottom) + getDimensionPixelSize(R.dimen.fab_size)).toFloat()
        bottomToolbar.translationY = (getDimensionPixelSize(R.dimen.bottom_toolbar_height) + getDimensionPixelSize(R.dimen.bottom_toolbar_margin)).toFloat()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

                if (webView.alpha < 1f) {
                    progressBarWebView.animate()
                            .alpha(0f)
                            .start()

                    webView.animate()
                            .alpha(1f)
                            .start()
                }
            }
        }

        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rootView.viewTreeObserver.removeOnPreDrawListener(this)

                fabParent.animate()
                        .translationY(0f)
//                        .setInterpolator(AccelerateInterpolator())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                bottomToolbar.animate()
                                        .translationY(0f)
//                                        .setInterpolator(AccelerateDecelerateInterpolator())
                                        .start()
                            }
                        })
                        .setStartDelay(300)
                        .start()

                return true
            }

        })

    }


    private fun initClicks() {


//        TooltipCompat.setTooltipText(iconBack, "返回")

        btnBack.setOnClickListener {
            finish()
        }

        btnHiragana.setOnClickListener {
            setBtnHiraganaState()
            loadWebViewData()
        }

        appBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                startTvTitleAnimator(state == State.COLLAPSED)
            }
        })

        floatingActionButton.setOnClickListener {
            if (news == null) {

                return@setOnClickListener
            }

            if (news?.voiceUrl?.isEmpty() != false) {

                return@setOnClickListener
            }

            news?.voiceUrl?.let { voiceUrl ->
                if (progressCircular.alpha > 0) {
                    return@setOnClickListener
                }

                if (mediaSource == null) {
                    mediaSource = createHlsMediaSource(voiceUrl)
                    mediaSource!!.addEventListener(Handler(), object : DefaultMediaSourceEventListener() {
                        override fun onLoadStarted(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
                            super.onLoadStarted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData)
                            Log.d(TAG, "onLoadStarted()")
                        }

                        override fun onLoadCanceled(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
                            super.onLoadCanceled(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData)
                            Log.d(TAG, "onLoadCanceled()")
                        }

                        override fun onLoadCompleted(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
                            super.onLoadCompleted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData)
                            Log.d(TAG, "onLoadCompleted()")
                        }

                        override fun onLoadError(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?, error: IOException?, wasCanceled: Boolean) {
                            super.onLoadError(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData, error, wasCanceled)
                            Log.d(TAG, "onLoadError()" + error?.message)
                        }
                    })
                    player.prepare(mediaSource)
                } else {
                    if (player.playbackState == Player.STATE_READY && player.playWhenReady) {
                        player.playWhenReady = false
                        setPlayIcon()
                    } else {
                        if (player.playbackState == Player.STATE_ENDED) {
                            player.seekTo(0L)
                        }
                        player.playWhenReady = true
                    }
                }
            }
        }
    }

    private fun setBtnHiraganaState() {
        when (levelHiragana) {
            ICON_LEVEL_HIRAGANA_A_FILL -> {
                levelHiragana = ICON_LEVEL_HIRAGANA_A_STROKE
                TooltipCompat.setTooltipText(btnHiragana, "显示假名")
            }
            ICON_LEVEL_HIRAGANA_A_STROKE -> {
                levelHiragana = ICON_LEVEL_HIRAGANA_A_FILL
                TooltipCompat.setTooltipText(btnHiragana, "隐藏假名")
            }
        }

        iconHiragana.setImageLevel(levelHiragana)
    }

    private fun createHlsMediaSource(url: String): HlsMediaSource {
        val uri = Uri.parse(url)
        return HlsMediaSource.Factory(
                MyCacheDataSourceFactory(this)
        ).createMediaSource(uri)
    }


    private fun startTvTitleAnimator(isShow: Boolean) {
        if (isShow) {
            if (tvTitleHideAnimator.isRunning) {
                tvTitleHideAnimator.cancel()
            }

            if (!tvTitleShowAnimator.isRunning && tvTitle.alpha != 1.0f) {
                tvTitleShowAnimator.start()
            }

        } else {
            if (tvTitleShowAnimator.isRunning) {
                tvTitleShowAnimator.cancel()
            }

            if (!tvTitleHideAnimator.isRunning && tvTitle.alpha != 0.0f) {
                tvTitleHideAnimator.start()
            }
        }
    }

    private fun setStatusBar() {
        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
        StatusBarUtils.addStatusBarHeightToMarginByView(this, toolbar)
    }

    private fun handleIntent(intent: Intent) {
        isDark = intent.getBooleanExtra(KEY_IS_DARK, false)
        news = intent.getParcelableExtra<News>(KEY_NEWS)
        newsDetails = intent.getParcelableExtra<NewsDetails>(KEY_NEWS_DETAILS)
    }

    private fun showData() {

        tvTitle.text = news?.title
        tvDate.text = news?.newsPrearrangedTimeFormat


        Glide.with(this)
                .load(if (news?.imageFilePath?.isEmpty() != false) news?.imageUrl else news?.imageFilePath)
                .fitCenter()
                .transition(DrawableTransitionOptions().crossFade())
                .placeholder(ColorDrawable(Color.parseColor(if (isDark) "#757575" else "#FFE0E0E0")))
                .into(imageView)

        loadWebViewData()

    }

    private fun loadWebViewData() {
        newsDetails?.let { data ->
            var style: String
            var html: String

            if (levelHiragana == ICON_LEVEL_HIRAGANA_A_FILL) {
                style = data.contentHtmlWithRubyStyle
                html = data.contentHtmlWithRuby
            } else {
                style = data.contentHtmlStyle
                html = data.contentHtml
            }

            var text = style + html
            if (isDark) {
                text = """
                    $style
                    <style> 
                        body { color: #5D949E; } 
                        .colorC { color: #BB86FC; }
                        .under {
			                padding-bottom: 4px;
			                border-bottom: 2px solid #757575;
		                }
                    </style>
                    $html
                    """
            }

            webView.loadData(text, "text/html; charset=utf-8", "UTF-8")
        }

    }

    private fun initWebView(): WebView {
        with(webView.settings) {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            defaultFontSize = 20
            minimumFontSize = 12
            textZoom = 110
            domStorageEnabled = true
        }
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        return webView
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                //You can use progress dialog to show user that video is preparing or buffering so please wait
                progressCircular.animate().alpha(1f).start()
                setPlayIcon()
            }
            Player.STATE_IDLE -> {
                //idle state
                setPlayIcon()
            }
            Player.STATE_READY -> {
                // dismiss your dialog here because our video is ready to play now
                progressCircular.animate().alpha(0f).start()
                setPauseIcon()
            }
            Player.STATE_ENDED -> {
                setPlayIcon()
            }
            // do your processing after ending of video
        }

    }

    private fun setPauseIcon() {
        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_pause))
    }

    private fun setPlayIcon() {
        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play))
    }

    private class AudioOnlyRenderersFactory(private val context: Context) : RenderersFactory {
        override fun createRenderers(eventHandler: Handler?, videoRendererEventListener: VideoRendererEventListener?, audioRendererEventListener: AudioRendererEventListener?, textRendererOutput: TextOutput?, metadataRendererOutput: MetadataOutput?, drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?): Array<Renderer> {
            return arrayOf<Renderer>(
                    MediaCodecAudioRenderer(context, MediaCodecSelector.DEFAULT, eventHandler, audioRendererEventListener)
            )
        }

    }

    companion object {
        const val KEY_NEWS = "news"
        const val KEY_NEWS_DETAILS = "details"
        const val KEY_IS_DARK = "is_dark"
        private const val ICON_LEVEL_HIRAGANA_A_FILL = 0
        private const val ICON_LEVEL_HIRAGANA_A_STROKE = 1

        fun start(context: Context, isDark: Boolean, news: News, newsDetails: NewsDetails) {
            context.startActivity(Intent(context, NewsDetailsActivity::class.java).apply {
                putExtra(KEY_IS_DARK, isDark)
                putExtra(KEY_NEWS, news)
                putExtra(KEY_NEWS_DETAILS, newsDetails)
            })
        }
    }
}