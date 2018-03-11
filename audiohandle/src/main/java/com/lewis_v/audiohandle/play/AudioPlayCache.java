package com.lewis_v.audiohandle.play;

/**
 * auth: lewis-v
 * time: 2018/2/28.
 */

public abstract class AudioPlayCache implements IAudioCache {
    protected AudioPlayData audioPlayData = new AudioPlayData();

    @Override
    public String handlePlayPath(String audioPath) {
        if (audioPath.startsWith("http")){//检测是否缓存
            String cacheData = getCachePath(audioPath);
            if (cacheData != null){//已有缓存,返回本地缓存地址
                return cacheData;
            }else{//无缓存,进行缓存
                String path = downCache(audioPath);
                if (path == null){
                    return audioPath;
                }else {
                    return path;
                }
            }
        }
        return audioPath;
    }

    /**
     * 获取缓存
     * @param audioPath
     * @return
     */
    abstract protected String getCachePath(String audioPath);

    /**
     * 下载缓存
     * @param audioPath
     * @return
     */
    abstract protected String downCache(String audioPath);

    @Override
    public void setAudioPlayData(AudioPlayData audioPlayData) {
        this.audioPlayData = audioPlayData;
    }

    @Override
    public AudioPlayData getAudioPlayData() {
        return audioPlayData;
    }
}
