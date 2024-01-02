package com.yic3.lib.util;

import android.media.MediaPlayer;
import android.net.Uri;

import com.blankj.utilcode.util.Utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 媒体播放工具
 */
public class MediaUtil {

    private MediaPlayer player;
    private List<EventListener> eventListeners = new CopyOnWriteArrayList<>();
    private String currentPath;

    private MediaUtil() {
        initPlayer();
        // OpusPlayer.getInstance().addEventListener(audioListener);
    }

    private void initPlayer() {
        player = new MediaPlayer();
        player.setOnPreparedListener(mp -> {
            mp.start();
        });
        player.setOnErrorListener((mp, what, extra) -> false);
        player.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
            }
            return false;
        });
    }

    private static MediaUtil instance = new MediaUtil();

    public static MediaUtil getInstance() {
        return instance;
    }

    public MediaPlayer getPlayer() {
        if (player == null) {
            initPlayer();
        }
        return player;
    }

    public boolean isPlaying() {
        return getPlayer().isPlaying();
    }

    private void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    private void removeEventListener(EventListener listener) {
        eventListeners.remove(listener);
    }

    public void playAudio(String filePath) {
        MediaPlayer player = getPlayer();
        try {
            currentPath = filePath;
            player.reset();
            player.setDataSource(filePath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAudio(int rawId) {
        MediaPlayer player = getPlayer();
        try {
            player.reset();
            player.setDataSource(Utils.getApp().getResources().openRawResourceFd(rawId));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAudio() {
        if (player != null) {
            player.stop();
        }
    }

    /**
     * 是当前文件，则停止-》播放，播放-》停止
     * 非当前文件，停止以前的，播放当前的
     */
    public void toggle(String filePath, EventListener listener) {
        if (filePath.equals(currentPath) && isPlaying()) {
            player.stop();
            return;
        }
        addEventListener(listener);
        playAudio(filePath);
    }

    public void stop() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
    }

    public long getDuration(String path) {
        MediaPlayer player = MediaPlayer.create(Utils.getApp(), Uri.parse(path));
        return player.getDuration();
    }


    /**
     * 播放器事件监听
     */
    public static abstract class EventListener {
        public abstract void onStop();
//        public void onFailed(){
//        }
        public void onProgress(long position,long ts){
        }
        public void onStarted(){
        }
    }
}
