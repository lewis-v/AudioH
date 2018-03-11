package com.lewis_v.audiohandle.recoder;

import android.os.Environment;

/**
 * auth: lewis-v
 * time: 2018/2/26.
 */

public class AudioRecoderData implements Cloneable{
    private int MAX_LENGTH = 1000 * 60;// 最大录音时长1000*60,60秒;超出的无效
    private int MIN_LENGTH = 500;//最小录音长度,太小的视为无效
    private int SAMPLEING_RATE = 200;//声音变化回调的采样率
    //文件路径
    private String filePath;
    //文件夹路径
    private String FolderPath = Environment.getExternalStorageDirectory()+"/data/chat/audio";
    private long startTime;
    private long endTime;

    AudioRecoderData() {
    }

    public int getMAX_LENGTH() {
        return MAX_LENGTH;
    }

    public AudioRecoderData setMAX_LENGTH(int MAX_LENGTH) {
        this.MAX_LENGTH = MAX_LENGTH;
        return this;
    }

    public int getSAMPLEING_RATE() {
        return SAMPLEING_RATE;
    }

    public AudioRecoderData setSAMPLEING_RATE(int SAMPLEING_RATE) {
        this.SAMPLEING_RATE = SAMPLEING_RATE;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public AudioRecoderData setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getFolderPath() {
        return FolderPath;
    }

    public AudioRecoderData setFolderPath(String folderPath) {
        FolderPath = folderPath;
        return this;
    }

    public long getStartTime() {
        return startTime;
    }

    public AudioRecoderData setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getEndTime() {
        return endTime;
    }

    public AudioRecoderData setEndTime(long endTime) {
        this.endTime = endTime;
        return this;
    }

    public int getMIN_LENGTH() {
        return MIN_LENGTH;
    }

    public AudioRecoderData setMIN_LENGTH(int MIN_LENGTH) {
        this.MIN_LENGTH = MIN_LENGTH;
        return this;
    }

    @Override
    public String toString() {
        return "AudioRecoderData{" +
                "MAX_LENGTH=" + MAX_LENGTH +
                ", MIN_LENGTH=" + MIN_LENGTH +
                ", SAMPLEING_RATE=" + SAMPLEING_RATE +
                ", filePath='" + filePath + '\'' +
                ", FolderPath='" + FolderPath + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
