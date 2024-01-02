package com.yic3.lib.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.concurrent.LinkedBlockingQueue;

public class AudioPlayer {

    public enum PlayState {
        idle,
        playing,
        pause
    }

    private int SAMPLE_RATE = 16000;
    private boolean isFinishSend = false;
    private AudioPlayerCallback audioPlayerCallback;
    private LinkedBlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue();
    private PlayState playState;

    public PlayState getPlayState() {
        return playState;
    }

    // 初始化播放器
    private int iMinBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT);

    private AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO
            , AudioFormat.ENCODING_PCM_16BIT,
            iMinBufSize * 8, AudioTrack.MODE_STREAM);
    private byte[] tempData;

    private Thread ttsPlayerThread;


    AudioPlayer(AudioPlayerCallback callback) {
        playState = PlayState.idle;
        audioTrack.play();
        audioPlayerCallback = callback;

        ttsPlayerThread = new Thread(() -> {
            while (true) {
                if (playState == PlayState.playing) {
                    if (audioQueue.size() == 0) {
                        if (isFinishSend) {
                            playState = PlayState.idle;
                            audioPlayerCallback.playOver();
                            isFinishSend = false;
                        } else {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        continue;
                    }
                    try {
                        tempData = audioQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (audioTrack != null) {
                        audioTrack.write(tempData, 0, tempData.length);
                    }
                } else {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ttsPlayerThread.start();
    }

    public void setAudioData(byte[] data) {
        audioQueue.offer(data);
        //非阻塞
    }

    public void isFinishSend(boolean isFinish) {
        isFinishSend = isFinish;
    }

    public boolean isPlaying() {
        return playState == PlayState.playing;
    }

    public void play() {
        playState = PlayState.playing;
        isFinishSend = false;
        audioTrack.play();
        audioPlayerCallback.playStart();
    }

    public void stop() {
        audioPlayerCallback.playOver();
        playState = PlayState.idle;
        audioQueue.clear();
        if (audioTrack != null) {
            audioTrack.flush();
            audioTrack.pause();
            audioTrack.stop();
        }
    }

    public void pause() {
        playState = PlayState.pause;
        if (audioTrack != null) {
            audioTrack.pause();
        }
    }

    public void resume() {
        if (audioTrack != null) {
            audioTrack.play();
        }
        playState = PlayState.playing;
    }

    public void initAudioTrack(int samplerate) {
        // 初始化播放器
        int iMinBufSize = AudioTrack.getMinBufferSize(samplerate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplerate,
                AudioFormat.CHANNEL_OUT_MONO
                , AudioFormat.ENCODING_PCM_16BIT,
                iMinBufSize * 10, AudioTrack.MODE_STREAM);
    }

    public void releaseAudioTrack() {
        if (audioTrack != null) {
            audioTrack.stop();
        }
    }

    public void setSampleRate(int sampleRate) {
        if (SAMPLE_RATE != sampleRate) {
            releaseAudioTrack();
            initAudioTrack(sampleRate);
            SAMPLE_RATE = sampleRate;
        }
    }
}
