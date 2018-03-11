package com.lewis_v.audiohandle.play;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * auth: lewis-v
 * time: 2018/2/27.
 */

public class AudioPlayImp implements IAudioPlay {
    public final static String TAG = "AudioPlayImp";
    private AudioPlayListener audioPlayListener;
    private MediaPlayer mPlayer;
    private AudioPlayStatus status = AudioPlayStatus.FREE;
    private String audioPath;//播放地址
    private IAudioCache iAudioCache;

    public AudioPlayImp() {

    }

    @Override
    public void init(Context context) {
        AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.setSpeakerphoneOn(false);
        }
    }

    public void play(String audioFile, AudioPlayMode mode) {
        if (status == AudioPlayStatus.PLAYING){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Playing"),"播放中");
            }
            return;
        }
        if (status == AudioPlayStatus.PAUSE){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Pause"),"播放已暂停");
            }
        }
        if (status == AudioPlayStatus.STOPING){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Stoping"),"播放停止中");
            }
        }
        status = AudioPlayStatus.PLAYING;
        try {
            Log.e(TAG,audioFile);
            this.audioPath = audioFile;
            if (audioPlayListener != null){
                audioPlayListener.onPlay(audioFile);
            }
            if (mPlayer != null){
                stop();
            }
            mPlayer = readyPlay(audioFile);
            setPlayMode(mPlayer,mode);
            mPlayer.prepare();
            mPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
            status = AudioPlayStatus.FREE;
            mPlayer = null;
            if (audioPlayListener != null){
                audioPlayListener.onFail(e,"播放失败");
            }
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void continuePlay() {

    }

    @Override
    public void stop() {
        if (status == AudioPlayStatus.FREE){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Free"),"未在播放中");
            }
            return;
        }
        if (status == AudioPlayStatus.PAUSE){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Pause"),"播放已暂停");
            }
            return;
        }
        if (status == AudioPlayStatus.STOPING){
            if (audioPlayListener != null){
                audioPlayListener.onFail(new RuntimeException("is Stoping"),"播放停止中");
            }
            return;
        }
        status = AudioPlayStatus.STOPING;
        try {
            mPlayer.stop();
        }catch (Exception e){}
        mPlayer.reset();
        mPlayer.release();
        status = AudioPlayStatus.FREE;
        mPlayer = null;
        if (audioPlayListener != null){
            audioPlayListener.onStop();
        }
    }

    @Override
    public void putERR(Exception e, String msg) {
        if (status == AudioPlayStatus.PLAYING){
            stop();
        }
        if (audioPlayListener != null){
            audioPlayListener.onFail(e,msg);
        }
    }

    /**
     * 准备播放对象
     * @return
     */
    protected MediaPlayer readyPlay(String audioFile) throws IOException {
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (audioPlayListener != null){
                    audioPlayListener.onFail(new RuntimeException("ERR"),"播放出错");
                }
                return false;
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {//播放结束
                stop();
            }
        });
        audioFile = handlePlayPath(audioFile);
        if (audioFile.startsWith("http")) {
            mPlayer.setDataSource(audioFile);
        }else {
            File file = new File(audioFile);
            FileInputStream fis = new FileInputStream(file);
            mPlayer.setDataSource(fis.getFD());
        }
        return mPlayer;
    }

    /**
     * 设置播放模式
     * @param mediaPlayer
     * @param mode
     * @return
     */
    protected MediaPlayer setPlayMode(MediaPlayer mediaPlayer,AudioPlayMode mode){
        if (mode == AudioPlayMode.RECEIVER) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            Log.e(TAG,"RECEIVER");
        }else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.e(TAG,"MUSIC");
        }
        return mediaPlayer;
    }

    /**
     * 对audioPath地址进行处理
     * @param audioPath
     * @return
     */
    protected String handlePlayPath(String audioPath){
        if (iAudioCache != null){
            return iAudioCache.handlePlayPath(audioPath);
        }
        return audioPath;
    }

    public void setAudioPlayListener(AudioPlayListener audioPlayListener) {
        this.audioPlayListener = audioPlayListener;
    }

    public AudioPlayStatus getStatus() {
        return status;
    }

    @Override
    public String getAudioPath() {
        return audioPath;
    }

    @Override
    public void setAudioCache(IAudioCache iAudioCache) {
        this.iAudioCache = iAudioCache;
    }
}
