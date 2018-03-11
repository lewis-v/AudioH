package com.lewis_v.audiohandle.recoder;

/**
 * auth: lewis-v
 * time: 2018/2/25.
 */

public interface AudioRecoderListener {
    void onStart();
    void onStop(AudioRecoderData audioRecoderData);
    void onFail(Exception e, String msg);
    void onCancel();
    void onSoundSize(int level);
}
