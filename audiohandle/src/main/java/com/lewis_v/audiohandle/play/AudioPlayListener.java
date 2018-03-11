package com.lewis_v.audiohandle.play;

/**
 * auth: lewis-v
 * time: 2018/2/27.
 */

public interface AudioPlayListener {
    void onPlay(String audioPath);
    void onProgress(int progress, int maxSize);
    void onPause();
    void onStop();
    void onFail(Exception e, String msg);
}
