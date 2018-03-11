package com.lewis_v.audiohandle.recoder;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

/**
 * auth: lewis-v
 * time: 2018/2/25.
 */

public class AudioRecoderManager {
    private IAudioRecoder iAudioRecoder;

    private static final class Instance{
        private static final AudioRecoderManager instance = new AudioRecoderManager();
    }

    private AudioRecoderManager() {
        iAudioRecoder = new AudioRecoderImp();
        //给一个默认的录音配置
        iAudioRecoder.setAudioData(new AudioRecoderData());
    }

    public static AudioRecoderManager getInstance(){
        return Instance.instance;
    }

    /**
     * 设置录音实现
     * @param impl
     * @return
     */
    public AudioRecoderManager setAudioImpl(IAudioRecoder impl){
        iAudioRecoder = impl;
        return this;
    }

    public AudioRecoderManager setAudioRecoderData(AudioRecoderData audioRecoderData){
        iAudioRecoder.setAudioData(audioRecoderData);
        return this;
    }

    public AudioRecoderManager setAudioRecoderListener(AudioRecoderListener audioRecoderListener){
        iAudioRecoder.setAudioRecoderListener(audioRecoderListener);
        return this;
    }

    public AudioRecoderManager start(Context context){
        if (isPermission(context)) {
            iAudioRecoder.start();
        }else {
            iAudioRecoder.putERR(new RuntimeException("no permission"),"无录音/读写权限");
        }
        return this;
    }

    public AudioRecoderManager stop(Context context){
        if (isPermission(context)) {
            iAudioRecoder.stop();
        }else {
            iAudioRecoder.putERR(new RuntimeException("no permission"),"无录音/读写权限");
        }
        return this;
    }

    public AudioRecoderManager destroy(Context context){
        if (isPermission(context)) {
            iAudioRecoder.destroy();
        }else {
            iAudioRecoder.putERR(new RuntimeException("no permission"),"无录音/读写权限");
        }
        return this;
    }

    public AudioRecoderManager cancel(Context context){
        if (isPermission(context)) {
            iAudioRecoder.cancel();
        }else {
            iAudioRecoder.putERR(new RuntimeException("no permission"),"无录音/读写权限");
        }
        return this;
    }

    /**
     * 检测是否有权限
     * @param context
     * @return
     */
    public boolean isPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                        == PERMISSION_DENIED) {
            return false;
        }
        return true;
    }
}
