package com.lewis_v.audiohandle.play;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * auth: lewis-v
 * time: 2018/2/28.
 */

public class AudioPlayDiskCacheImp extends AudioPlayCache {

    /**
     * 获取缓存
     * @param audioPath
     * @return
     */
    protected String getCachePath(String audioPath){
        String fileName = getNameFromUrl(audioPath);
        File file = new File(audioPlayData.getCACHE_DIR()+"/"+fileName);
        if (file.exists()){
            return file.getAbsolutePath();
        }else {
            return null;
        }
    }

    /**
     * 下载缓存,返回缓存后的地址
     * @param audioPath
     * @return
     */
    protected String downCache(String audioPath){
        File file = null;
        InputStream input = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(audioPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            input = connection.getInputStream();
            byte[] buf = new byte[2048];
            int len = 0;
            String savePath = isExistDir(audioPlayData.getCACHE_DIR(), getNameFromUrl(audioPath));
            file = new File(savePath);
            fos = new FileOutputStream(file);
            while ((len = input.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush(); // 下载完成
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (input != null) input.close();
            } catch (IOException e) { }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) { }
        }
        if (file == null){
            return null;
        }
        return file.getAbsolutePath();
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir,String fileName) throws IOException { // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.exists()){
            downloadFile.mkdirs();
        }
        downloadFile = new File(saveDir,fileName);
        if (!downloadFile.exists()){
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
