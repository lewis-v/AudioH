package com.lewis_v.audiohandle.play;

import android.os.Environment;

import java.io.File;

/**
 * auth: lewis-v
 * time: 2018/3/11.
 */

public class AudioPlayData {
    private String CACHE_DIR = Environment.getExternalStorageDirectory()+"/data/chat/audio";//默认缓存地址

    AudioPlayData(){

    }

    public String getCACHE_DIR() {
        return CACHE_DIR;
    }

    public AudioPlayData setCACHE_DIR(String CACHE_DIR) {
        this.CACHE_DIR = CACHE_DIR;
        return this;
    }

    @Override
    public String toString() {
        return "AudioPlayData{" +
                "CACHE_DIR='" + CACHE_DIR + '\'' +
                '}';
    }



    public class AudioPlayDataBuilder{
        private AudioPlayData audioPlayData = new AudioPlayData();

        public AudioPlayDataBuilder setCacheDir(String cacheDir){
            File file = new File(cacheDir);
            if (file.exists() && !file.isDirectory()){
                throw new RuntimeException("the path is not directory");
            }
            audioPlayData.setCACHE_DIR(cacheDir);
            return this;
        }

        public AudioPlayData create(){
            return audioPlayData;
        }

    }
}
