package com.yic3.lib.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

class AudioTrackUtil {

    private lateinit var mAudioTrack: AudioPlayer

    fun init(playListener: AudioPlayerCallback) {
        mAudioTrack = AudioPlayer(playListener)
    }

    fun play(file: File) {
        if (mAudioTrack.playState == AudioPlayer.PlayState.pause) {
            mAudioTrack.resume()
            return
        }
        if (mAudioTrack.isPlaying) {
            return
        }
        mAudioTrack.stop()
        mAudioTrack.play()
        val inputStream = FileInputStream(file)

        GlobalScope.launch(Dispatchers.IO) {
            while (inputStream.available() > 0) {
                val tempBuffer = ByteArray(1024 * 8)
                val readCount = inputStream.read(tempBuffer)
                if (readCount > 0) {
                    mAudioTrack.setAudioData(tempBuffer)
                }
            }
            mAudioTrack.isFinishSend(true)
        }
    }

    fun playByte(tempBuffer: ByteArray) {
        mAudioTrack.setAudioData(tempBuffer)
    }

    fun finishData() {
        mAudioTrack.isFinishSend(true)
    }

    fun play() {
        mAudioTrack.play()
    }

    fun stop() {
        mAudioTrack.stop()
    }

    fun pause() {
        mAudioTrack.pause()
    }

    fun resume() {
        mAudioTrack.resume()
    }

    fun release() {
        mAudioTrack.releaseAudioTrack()
    }

    fun isPlaying(): Boolean {
        return mAudioTrack.isPlaying
    }

}