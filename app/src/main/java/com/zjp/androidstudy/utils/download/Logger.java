package com.zjp.androidstudy.utils.download;

import android.text.TextUtils;
import android.util.Log;

import com.zjp.androidstudy.utils.AppContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class Logger {
    private static final String TAG = "Logger";
    private String fileName;    //下载的文件名字
    private String url;         //下载连接
    private int threadCount; //线程数量
    private LogProperties properties;

    private File logFile;


    public Logger(String fileName, String url, int threadCount) {
        this.fileName = fileName;
        this.url = url;
        this.threadCount = threadCount;
        logFile = new File(AppContext.getContext().getExternalCacheDir() + "/download.log");
    }

    /**
     * 是否可以断点下载
     *
     * @return
     */
    public boolean breakpoint() {
        LogProperties prop = null;
        if (logFile.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(logFile));
                prop = (LogProperties) ois.readObject();
            } catch (Exception e) {
                logFile.delete();
                e.printStackTrace();
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        logFile.delete();
                        e.printStackTrace();
                    }
                }
            }
        }
        //有缓存对比和上次文件是否一样
        if (prop != null && prop.threadCount == this.threadCount && TextUtils.equals(fileName, prop.fileName)
                && TextUtils.equals(prop.url, url)) {
            this.properties = prop;
            return true;
        }
        //没有缓存
        this.properties = new LogProperties(fileName, url, threadCount);
        return false;
    }

    /**
     * 写入缓存
     *
     * @param threadID   线程id
     * @param length     写入的文件长度
     * @param lowerBound 起始位置
     * @param upperBound 结束位置
     */
    public void write(int threadID, long length, long lowerBound, long upperBound) {
        properties.setWroteSize(properties.getWroteSize() + length);    //写入总大小
        properties.update(threadID, lowerBound);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(logFile));
            oos.writeObject(properties);
        } catch (Exception e) {
            Log.e(TAG, "write: " + e.getMessage());
        }
    }

    public Long getWroteSize() {
        return properties.getWroteSize();
    }

    public LogProperties getProperties() {
        return properties;
    }

    public void clearCache() {
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    static class LogProperties implements Serializable {
        private String fileName;    //下载的文件名字
        private String url;         //下载连接
        private int threadCount; //线程数量
        private HashMap<String, Long> threadInfo; //线程id- 线程起始
        private Long wroteSize;
        private Long fileSize;

        public LogProperties(String fileName, String url, int threadCount) {
            this.fileName = fileName;
            this.url = url;
            this.threadCount = threadCount;
            this.wroteSize = 0L;
            this.threadInfo = new HashMap<>(threadCount);
            createThreadKV();
        }

        public void createThreadKV() {
            for (int i = 0; i < threadCount; i++) {
                threadInfo.put("thread-" + i, 0L);
            }
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(int threadCount) {
            this.threadCount = threadCount;
        }

        public HashMap<String, Long> getThreadInfo() {
            return threadInfo;
        }

        public void setThreadInfo(HashMap<String, Long> threadInfo) {
            this.threadInfo = threadInfo;
        }

        public Long getWroteSize() {
            return wroteSize;
        }

        public void setWroteSize(Long wroteSize) {
            this.wroteSize = wroteSize;
        }

        public void update(int threadID, long lowerBound) {
            threadInfo.put("thread-" + threadID, lowerBound);
        }
    }
}
