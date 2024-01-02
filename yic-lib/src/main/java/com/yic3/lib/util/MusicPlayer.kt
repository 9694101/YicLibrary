/*
 * Copyright (c) 2019 Naman Dwivedi.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package com.yic3.lib.util

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias OnPrepared<T> = T.() -> Unit
typealias OnError<T> = T.(error: Throwable) -> Unit
typealias OnCompletion<T> = T.() -> Unit
typealias OnProgress<T> = T.(progress: Int) -> Unit

/**
 * An injectable wrapper around [MediaPlayer].
 *
 * @author Aidan Follestad (@afollestad)
 */
interface MusicPlayer {
    fun play()

    fun setSource(path: String): Boolean

    fun setSource(uri: Uri): Boolean

    fun prepare()

    fun seekTo(position: Int)

    fun isPrepared(): Boolean

    fun isPlaying(): Boolean

    fun position(): Int

    fun duration(): Int

    fun pause()

    fun stop()

    fun reset()

    fun release()

    fun onPrepared(prepared: OnPrepared<MusicPlayer>)

    fun onError(error: OnError<MusicPlayer>)

    fun onCompletion(completion: OnCompletion<MusicPlayer>)

    fun onProgress(progress: OnProgress<MusicPlayer>)
}

class RealMusicPlayer(internal val context: Application) : MusicPlayer,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private var _player: MediaPlayer? = null
    private val player: MediaPlayer
        get() {
            if (_player == null) {
                _player = createPlayer(this)
            }
            return _player ?: throw IllegalStateException("Impossible")
        }

    private var didPrepare = false
    private var onPrepared: OnPrepared<MusicPlayer> = {}
    private var onError: OnError<MusicPlayer> = {}
    private var onCompletion: OnCompletion<MusicPlayer> = {}
    private var onProgress: OnProgress<MusicPlayer> = {}

    override fun play() {
        player.start()
    }

    override fun setSource(path: String): Boolean {
        try {
            player.setDataSource(path)
        } catch (e: Exception) {
            onError(this, e)
            return false
        }
        return true
    }

    override fun setSource(uri: Uri): Boolean {
        try {
            player.setDataSource(context, uri)
        } catch (e: Exception) {
            onError(this, e)
            return false
        }
        return true
    }

    override fun prepare() {
        player.prepareAsync()
    }

    override fun seekTo(position: Int) {
        player.seekTo(position * 1000)
    }

    override fun isPrepared() = didPrepare

    override fun isPlaying() = player.isPlaying

    override fun position() = player.currentPosition / 1000

    override fun duration() = player.duration / 1000

    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.stop()
    }

    override fun reset() {
        player.reset()
    }

    override fun release() {
        progressJob?.cancel()
        player.release()
        _player = null
    }

    override fun onPrepared(prepared: OnPrepared<MusicPlayer>) {
        this.onPrepared = prepared
    }

    override fun onError(error: OnError<MusicPlayer>) {
        this.onError = error
    }

    override fun onCompletion(completion: OnCompletion<MusicPlayer>) {
        this.onCompletion = completion
    }

    override fun onProgress(progress: OnProgress<MusicPlayer>) {
        this.onProgress = progress
    }

    var progressJob: Job? = null

    // Callbacks from stock MediaPlayer...
    private fun startProgressListener() {
        progressJob?.cancel()
        progressJob = GlobalScope.launch(Dispatchers.Main) {
            while (true) {
                onProgress(this@RealMusicPlayer.position())
                withContext(Dispatchers.IO) {
                    Thread.sleep(1 * 1000)
                }
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        didPrepare = true
        onPrepared(this)
        startProgressListener()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        didPrepare = false
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        onCompletion(this)
    }
}

private fun createPlayer(owner: RealMusicPlayer): MediaPlayer {
    return MediaPlayer().apply {
        setWakeMode(owner.context, PowerManager.PARTIAL_WAKE_LOCK)
        val attr = AudioAttributes.Builder().apply {
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            setUsage(AudioAttributes.USAGE_MEDIA)
        }.build()
        setAudioAttributes(attr)
        setOnPreparedListener(owner)
        setOnCompletionListener(owner)
        setOnErrorListener(owner)
    }
}
