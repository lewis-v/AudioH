package com.lewis_v.audiohandle.recoder;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;

/**
 * auth: lewis-v
 * time: 2018/2/25.
 */

public class AudioRecoderImp implements IAudioRecoder {
    private static final String TAG = "AudioRecoderImp";
    private AudioRecoderData audioRecoderData;
    private MediaRecorder mMediaRecorder;
    private AudioRecoderListener audioRecoderListener;
    private Handler handler;
    private AudioRecoderThread audioRecoderThread;
    private final Object lock = new Object();//用来同步停止线程的锁
    private AudioRecoderStatus status = AudioRecoderStatus.FREE;//录音状态

    public AudioRecoderImp() {
        handler = new Handler(Looper.getMainLooper());
    }

    public void start(){
        if (status == AudioRecoderStatus.RECODERING){//录音中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is Running"),"正在录音中");
            }
            return;
        }
        status = AudioRecoderStatus.RECODERING;
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    if (audioRecoderListener != null){
                        audioRecoderListener.onFail(new RuntimeException("fail"),"录音出错");
                    }
                }
            });
            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {//达到最大的时间
                        stop();
                    }
                }
            });
        }
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

            audioRecoderData.setFilePath( audioRecoderData.getFolderPath() + "AUDIO_"+System.currentTimeMillis() + ".aac" );
            Log.e(TAG,audioRecoderData.getFilePath());
            File file = new File(audioRecoderData.getFolderPath());
            if (!file.exists()){
                file.mkdirs();
            }
            file = new File(audioRecoderData.getFilePath());
            if (!file.exists()){
                file.createNewFile();
            }
            mMediaRecorder.setOutputFile(audioRecoderData.getFilePath());
            mMediaRecorder.setMaxDuration(audioRecoderData.getMAX_LENGTH());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            audioRecoderData.setStartTime(System.currentTimeMillis());
            if (audioRecoderListener != null){
                audioRecoderListener.onStart();
            }
            synchronized (lock) {
                audioRecoderThread = new AudioRecoderThread();
                handler.postDelayed(audioRecoderThread, audioRecoderData.getSAMPLEING_RATE());
            }
            Log.e("fan", "startTime" + audioRecoderData.getStartTime());
        } catch (IllegalStateException e) {
            status = AudioRecoderStatus.FREE;
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(e,"录音出错");
            }
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            status = AudioRecoderStatus.FREE;
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(e,"录音出错");
            }
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public void stop() {
        if (mMediaRecorder == null) {
            return;
        }
        if (status == AudioRecoderStatus.FREE ){//空闲中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is FREE"),"录音空闲中,无需停止");
            }
            return;
        }else if (status == AudioRecoderStatus.STOPING){//停止中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is STOPING"),"录音停止中");
            }
            return;
        }
        status = AudioRecoderStatus.STOPING;
        synchronized (lock) {
            handler.removeCallbacks(audioRecoderThread);
        }
        audioRecoderData.setEndTime(System.currentTimeMillis());
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }catch (RuntimeException e){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            File file = new File(audioRecoderData.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        try{
            if (audioRecoderData.getEndTime() - audioRecoderData.getStartTime() < audioRecoderData.getMIN_LENGTH()){
                if (audioRecoderListener != null){
                    audioRecoderListener.onFail(new RuntimeException("AudioRecoder too short"),"说话时间太短啦");
                }
            }else if (new File(audioRecoderData.getFilePath()).exists()){
                if (audioRecoderListener != null) {
                    audioRecoderListener.onStop((AudioRecoderData) audioRecoderData.clone());
                }
                Log.e(TAG, audioRecoderData.getFilePath());
            }

        }catch (CloneNotSupportedException e) {
            e.printStackTrace();
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(e,"录音出错");
            }
        }
        status = AudioRecoderStatus.FREE;
    }

    @Override
    public void cancel() {
        if (mMediaRecorder == null) {
            return;
        }
        if (status == AudioRecoderStatus.FREE ){//空闲中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is FREE"),"录音空闲中,无需停止");
            }
            return;
        }else if (status == AudioRecoderStatus.STOPING){//停止中
            if (audioRecoderListener != null){
                audioRecoderListener.onFail(new RuntimeException("AudioRecoder is STOPING"),"录音停止中");
            }
            return;
        }
        status = AudioRecoderStatus.STOPING;
        synchronized (lock) {
            handler.removeCallbacks(audioRecoderThread);
        }
        audioRecoderData.setEndTime(System.currentTimeMillis());
        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        try{
            mMediaRecorder.stop();
        }catch (Exception e){

        }
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;

        File file = new File(audioRecoderData.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        if (audioRecoderListener != null){
            audioRecoderListener.onCancel();
        }
        audioRecoderData.setFilePath("");
        status = AudioRecoderStatus.FREE;
    }

    @Override
    public void destroy() {
        if (status == AudioRecoderStatus.RECODERING){
            stop();
            audioRecoderListener = null;
        }
    }

    @Override
    public void putERR(Exception e, String msg) {
        if (status == AudioRecoderStatus.RECODERING) {
            stop();
        }
        if (audioRecoderListener != null){
            audioRecoderListener.onFail(e,msg);
        }
    }

    @Override
    public void setAudioData(AudioRecoderData audioData) {
        this.audioRecoderData = audioData;
    }


    //获取录音分贝
    private class AudioRecoderThread implements Runnable{

        @Override
        public void run() {
            if (mMediaRecorder != null){
                int ratio = mMediaRecorder.getMaxAmplitude()/600;
                int DB = 0;
                if (ratio > 1) {
                    DB = (int)(20 * Math.log10(ratio));//分贝
                }
                if (audioRecoderListener != null){
                    audioRecoderListener.onSoundSize(DB);
                }
                synchronized (lock) {
                    audioRecoderThread = new AudioRecoderThread();
                    handler.postDelayed(audioRecoderThread, audioRecoderData.getSAMPLEING_RATE());
                }
            }
        }
    }

    @Override
    public void setAudioRecoderListener(AudioRecoderListener audioRecoderListener) {
        this.audioRecoderListener = audioRecoderListener;
    }

}
