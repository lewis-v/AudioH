package com.lewis_v.audiohandle.play;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

/**
 * auth: lewis-v
 * time: 2018/2/27.
 */

public class AudioPlayManager {
    private static final String TAG = "AudioPlayManager";
    private static final int PLAY = 0;//播放
    private static final int PAUSE = 1;//暂停
    private static final int CONTINUE = 2;//继续播放
    private static final int STOP = 3;//停止
    private static final int QUIT = -1;//退出消息队列

    private static volatile AudioPlayManager instance = null;
    private IAudioPlay iAudioPlay;
    private IClearCache iClearCache;
    private IAudioCache iAudioCache;
    private ExecutorService executors;
    private PlayThread playThread;

    public static AudioPlayManager getInstance(){
        if (instance == null){
            synchronized (AudioPlayManager.class){
                if (instance == null){
                    instance = new AudioPlayManager();
                }
            }
        }
        return instance;
    }

    private AudioPlayManager() {
        iAudioPlay = new AudioPlayImp();
        iAudioCache = new AudioPlayDiskCacheImp();
        iAudioPlay.setAudioCache(iAudioCache);
        iClearCache = new ClearCache(iAudioCache.getAudioPlayData().getCACHE_DIR());
        executors = Executors.newCachedThreadPool();
        playThread = new PlayThread();
        executors.execute(playThread);
    }

    public AudioPlayManager init(Context context){
        iAudioPlay.init(context.getApplicationContext());
        return this;
    }

    public AudioPlayManager setPlayListener(AudioPlayListener audioPlayListener){
        (iAudioPlay).setAudioPlayListener(audioPlayListener);
        return this;
    }

    /**
     * 设置缓存模式，默认为磁盘缓存，可自定义缓存模式
     * @param iAudioCache
     * @return
     */
    public AudioPlayManager setAudioCache(IAudioCache iAudioCache){
        iAudioPlay.setAudioCache(iAudioCache);
        return this;
    }

    public AudioPlayManager setAudioPlayData(AudioPlayData audioPlayData){

        return this;
    }

    /**
     * 播放音频
     * @param audioPath
     * @return
     */
   public AudioPlayManager play(String audioPath ,Context context, AudioPlayMode mode){
       if (isPermission(context)) {
           if ((iAudioPlay).getStatus() == AudioPlayStatus.PLAYING && (iAudioPlay).getAudioPath().equals(audioPath)) {
               stop();
           } else {
               Message message = new Message();
               message.what = PLAY;
               message.obj = audioPath;
               message.arg1 = mode.getTypeName();
               playThread.handler.sendMessage(message);
           }
       }else {
           iAudioPlay.putERR(new RuntimeException("no permission"),"无读取权限");
       }
        return this;
   }

    /**
     * 停止播放
     * @return
     */
   public AudioPlayManager stop(){
       playThread.handler.sendEmptyMessage(STOP);
       return this;
   }

    /**
     * 获取当前播放状态
     * @return
     */
   public AudioPlayStatus getPlayStatus(){
       return (iAudioPlay).getStatus();
   }

    private class PlayThread extends Thread{
        Handler handler;
        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case PLAY:
                            if (msg.obj != null) {
                                play(msg.obj.toString(),AudioPlayMode.getByTypeName(msg.arg1));
                            }
                            break;
                        case PAUSE:

                            break;
                        case CONTINUE:

                            break;
                        case STOP:
                            stopPLay();
                            break;
                        case QUIT:
                            stopPLay();
                            Looper.myLooper().quit();
                            break;
                    }
                    super.handleMessage(msg);
                }
            };
            Looper.loop();
        }

        private void play(String audioPath,AudioPlayMode mode){
            while ((iAudioPlay).getStatus() == AudioPlayStatus.STOPING){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            if ((iAudioPlay).getStatus() != AudioPlayStatus.FREE ){
                iAudioPlay.stop();
            }
            iAudioPlay.play(audioPath,mode);
        }

        private void stopPLay(){
            iAudioPlay.stop();
        }
    }

    /**
     * 获取缓存大小，此过程可能耗时，建议开子线程
     * @return
     */
    public long getCacheSize(Context context){
        if (isPermission(context)) {
            return iClearCache.getCacheSize();
        }else {
            iAudioPlay.putERR(new RuntimeException("no permission"),"无读取权限");
        }
        return 0;
    }

    /**
     * 清理缓存，此过程可能耗时，建议开子线程
     * @return
     */
    public long clearCache(Context context){
        if (isPermission(context)) {
            return iClearCache.clearCache();
        }else {
            iAudioPlay.putERR(new RuntimeException("no permission"),"无读取权限");
        }
        return 0;
    }

    /**
     * 释放资源,按道理不用调用释放的，如果调用了的话会停止播放线程，释放此单例
     */
    public void destory(){
        if (playThread != null){
            playThread.handler.sendEmptyMessage(QUIT);
            playThread.interrupt();
            playThread = null;
        }
        executors.shutdownNow();
        executors = null;
        instance = null;
    }

    /**
     * 检测是否有权限
     * @param context
     * @return
     */
    public boolean isPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PERMISSION_DENIED && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PERMISSION_DENIED) {
            return false;
        }
        return true;
    }
}
