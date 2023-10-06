package com.app.synchealth.utils

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.app.synchealth.MainActivity
import com.app.synchealth.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class ExpoPlayerUtils {

    private var player: SimpleExoPlayer? = null
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var fullscreen = false

    fun initializePlayer(context: Context,playerView: PlayerView,videoUrl:String?) {
        player = SimpleExoPlayer.Builder(context).build()
        playerView.player = player
        val imgFullScreen = (context as MainActivity).findViewById<ImageView>(R.id.imgFullScreen)
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri,context)
        //player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.prepare(mediaSource!!, false, false)
        imgFullScreen.setOnClickListener {
            if (fullscreen) {
                imgFullScreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.exo_controls_fullscreen_enter
                    )
                )
                context.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                if (context.supportActionBar != null) {
                    context.supportActionBar!!.show()
                }
                context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val params: ConstraintLayout.LayoutParams =
                    playerView.layoutParams as ConstraintLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = (250 * context.resources
                    .displayMetrics.density).toInt()
                playerView.layoutParams = params
                fullscreen = false
            } else {
                imgFullScreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.exo_controls_fullscreen_exit
                    )
                )
                context.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                if (context.supportActionBar != null) {
                    context.supportActionBar!!.hide()
                }
                //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params =
                    playerView.layoutParams as ConstraintLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                playerView.layoutParams = params
                fullscreen = true
            }
        }
    }

    private fun buildMediaSource(uri: Uri,context: Context): MediaSource? {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, "exoplayer-codelab")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
    }

    fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }
}