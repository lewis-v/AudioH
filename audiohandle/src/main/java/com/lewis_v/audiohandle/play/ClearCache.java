package com.lewis_v.audiohandle.play;

import java.io.File;

/**
 * auth: lewis-v
 * time: 2018/3/11.
 */

public class ClearCache implements IClearCache{
    public String clearDir;

    public ClearCache(String clearDir) {
        this.clearDir = clearDir;
    }

    @Override
    public long getCacheSize() {
        File file = new File(clearDir);
        if (file.exists()){
            return getFileSize(file);
        }else {
            return 0;
        }
    }

    @Override
    public long clearCache() {
        File file = new File(clearDir);
        if (file.exists()) {
            return deleteFile(file);
        }else {
            return 0;
        }
    }

    /**
     * 删除文件夹及文件夹内的文件
     * @param file
     * @return 删除的文件大小
     */
    public long deleteFile(File file){
        long clearSize = 0;
        if (file.exists()) {//文件存在才操作
            if (file.isFile()) {//路径为一个文件,删除文件
                clearSize += file.length();
                file.delete();
            } else if (file.isDirectory()) {//路径为目录,删除路径下的文件
                for (File newFile :file.listFiles()){
                    clearSize += deleteFile(newFile);
                }
                file.delete();
            }
        }
        return clearSize;
    }

    public long getFileSize(File file){
        long fileSize = 0;
        if (file.exists()) {//文件存在才操作
            if (file.isFile()) {//路径为一个文件,删除文件
                fileSize += file.length();
            } else if (file.isDirectory()) {//路径为目录,删除路径下的文件
                for (File newFile :file.listFiles()){
                    fileSize += getFileSize(newFile);
                }
            }
        }
        return fileSize;
    }
}
