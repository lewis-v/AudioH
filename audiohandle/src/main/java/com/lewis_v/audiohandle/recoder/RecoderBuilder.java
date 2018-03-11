package com.lewis_v.audiohandle.recoder;

/**
 * auth: lewis-v
 * time: 2018/3/11.
 */

public abstract class RecoderBuilder {
    /**
     * 设置录音最大时间长度
     * @param length
     * @return
     */
    public abstract RecoderBuilder setMAX_LENGTH(int length);

    /**
     * 设置录音最小时间长度
     * @param length
     * @return
     */
    public abstract RecoderBuilder setMIN_LENGTH(int length);

    /**
     * 设置声音采样回调时间
     * @param rate
     * @return
     */
    public abstract RecoderBuilder setSAMPLEING_RATE(int rate);

    /**
     * 设置存储的目录
     * @param saveFolderPath
     * @return
     */
    public abstract RecoderBuilder setSaveFolderPath(String saveFolderPath);

    /**
     * 创建设置好的实体对象
     * @return
     */
    public abstract AudioRecoderData create();
}
