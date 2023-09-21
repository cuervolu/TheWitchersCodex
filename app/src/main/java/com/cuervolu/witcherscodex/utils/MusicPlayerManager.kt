package com.cuervolu.witcherscodex.utils

import android.content.Context
import android.media.MediaPlayer
import com.cuervolu.witcherscodex.R

object MusicPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentSongIndex = 0
    private val songs = intArrayOf(
        R.raw.the_vagabond,
        R.raw.kaer_morhen,
        R.raw.spikeroog,
        R.raw.the_tree_when_we_sat_once,
        R.raw.the_wolven_storm,
        R.raw.whispers_of_oxenfurt
    )
    private var isPaused = false

    fun initialize(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setOnCompletionListener {
                playNextSong(context)
            }
        }
    }

    fun startPlaying(context: Context) {
        if (mediaPlayer == null) {
            initialize(context)
        }
        if (!mediaPlayer?.isPlaying!! && !isPaused) {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(context.resources.openRawResourceFd(songs[currentSongIndex]))
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        }
    }

    fun pausePlaying() {
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer?.pause()
            isPaused = true
        }
    }

    fun resumePlaying() {
        if (!mediaPlayer?.isPlaying!! && isPaused) {
            mediaPlayer?.start()
            isPaused = false
        }
    }

    fun stopPlaying() {
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer?.pause() // Pausa en lugar de stop
        }
    }

    private fun playNextSong(context: Context) {
        currentSongIndex = (currentSongIndex + 1) % songs.size
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(context.resources.openRawResourceFd(songs[currentSongIndex]))
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

    fun setCurrentSongIndex(index: Int) {
        currentSongIndex = index
    }
}
