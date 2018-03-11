package com.lewis_v.audiohandle.recoder;

/**
 * auth: lewis-v
 * time: 2018/2/25.
 */

public interface IAudioRecoder {
    void start();
    void stop();
    void cancel();
    void destroy();
    void putERR(Exception e, String msg);
    void setAudioData(AudioRecoderData audioData);
    void setAudioRecoderListener(AudioRecoderListener audioRecoderListener);
}
