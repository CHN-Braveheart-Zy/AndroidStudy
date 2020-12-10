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
import java.nio.channels.Channels;
import java.util.HashMap;

public class Logger {
    private static final String TAG = "Logger";
    private String fileName;    //下载的文件名字
    private String url;         //下载连接
    private int threadCount; //线程数量
    private MyProp prop;
    private ObjectOutputStream oos;


    private String logPath; // 下载的文件的名字
    private File file;


    public Logger(String filePath, String url, int threadCount) {
        this.fileName = filePath;
        this.url = url;
        this.threadCount = threadCount;
        file = new File(AppContext.getContext().getExternalCacheDir() + "/download.log");
    }

    /**
     * 是否有需要重新开始
     *
     * @return
     */
    public boolean breakpoint() {
        MyProp prop = null;
        if (file.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                prop = (MyProp) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //有缓存
        if (prop != null && prop.threadCount == this.threadCount && TextUtils.equals(fileName, prop.fileName)
                && TextUtils.equals(prop.url, url)) {
            this.prop = prop;
            return true;
        }
        //有缓存   对比和上次文件是否一样
        this.prop = new MyProp(fileName, url, threadCount);
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
        prop.setWroteSize(prop.getWroteSize() + length);    //写入总大小
        prop.update(threadID, lowerBound);
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(prop);
        } catch (Exception e) {
            Log.e(TAG, "write: " + e.getMessage());
        }
    }

    public Long getWroteSize() {
        return prop.getWroteSize();
    }

    public MyProp getProp() {
        return prop;
    }

    static class MyProp implements Serializable {
        private String fileName;    //下载的文件名字
        private String url;         //下载连接
        private int threadCount; //线程数量
        private HashMap<String, Long> threadInfo; //线程id- 线程起始
        private Long wroteSize;

        public MyProp(String fileName, String url, int threadCount) {
            this.fileName = fileName;
            this.url = url;
            this.threadCount = threadCount;
            this.wroteSize = 0L;
            this.threadInfo = new HashMap<>(threadCount);
            initThreadInfo();
        }

        public void initThreadInfo() {
            for (int i = 0; i < threadCount; i++) {
                threadInfo.put("thread-" + i, 0L);
            }
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
