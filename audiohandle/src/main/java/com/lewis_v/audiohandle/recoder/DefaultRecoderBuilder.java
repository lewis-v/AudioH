package com.lewis_v.audiohandle.recoder;

import java.io.File;

/**
 * auth: lewis-v
 * time: 2018/3/11.
 */

public class DefaultRecoderBuilder extends RecoderBuilder {
    private AudioRecoderData audioRecoderData = new AudioRecoderData();

    @Override
    public RecoderBuilder setMAX_LENGTH(int length) {
        audioRecoderData.setMAX_LENGTH(length);
        return this;
    }

    @Override
    public RecoderBuilder setMIN_LENGTH(int length) {
        audioRecoderData.setMIN_LENGTH(length);
        return this;
    }

    @Override
    public RecoderBuilder setSAMPLEING_RATE(int rate) {
        audioRecoderData.setSAMPLEING_RATE(rate);
        return this;
    }

    @Override
    public RecoderBuilder setSaveFolderPath(String saveFolderPath) {
        File file = new File(saveFolderPath);
        if (file.exists()){
            if (!file.isDirectory()){
                throw new RuntimeException("the path is not a directory");
            }
        }
        audioRecoderData.setFolderPath(saveFolderPath);
        return this;
    }

    @Override
    public AudioRecoderData create() {
        return audioRecoderData;
    }
}
