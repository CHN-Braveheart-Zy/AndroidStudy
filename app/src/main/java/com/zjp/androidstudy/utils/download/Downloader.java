package com.zjp.androidstudy.utils.download;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Downloader {
    private static final String TAG = "Downloader";
    private String url;
    private AtomicBoolean canceled;
    private int threadCount;
    private DownloadFile file; // 下载的文件对象
    private Object lock;
    private String fileName;
    private String logName;
    private long beginTime;
    private Logger logger;
    private long fileSize;


    public static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CPU_COUNT,
            CPU_COUNT + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(64),
            Executors.defaultThreadFactory());

    public Downloader(String url) {
        this(url, 4);
    }

    public Downloader(String url, int threadCount) {
        this(url, threadCount, null);
    }

    public Downloader(String url, int threadCount, String path) {
        this.url = url;
        this.threadCount = threadCount;
        this.fileName = path == null ? url.substring(url.lastIndexOf("/") + 1) : path;
        this.canceled = new AtomicBoolean(false);
        this.logName = path + ".log";
        this.logger = new Logger(logName, url, threadCount);
        this.lock = new Object();
    }


    public void start() {
        canceled.compareAndSet(true,false);
        THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                boolean restartable = logger.breakpoint();
                if (restartable) {
                    System.out.printf(" 继续上次下载进度[已下载：%.2fMB]：%s\n", logger.getWroteSize() / 1014.0 / 1024.0, url);
                } else {
                    Log.d(TAG, "开始新的下载");
                }
                System.out.println(" 开始下载：" + url);
                if ((fileSize = getFileSize()) < 0) return;
                System.out.printf(" 文件大小：%.2fMB\n", (fileSize / 1024.0f / 1024.0f));

                beginTime = System.currentTimeMillis();
                try {
                    file = new DownloadFile(fileName, fileSize,logger);
                    if (restartable) {
                        file.setWroteSize(logger.getWroteSize());
                    }
                    // 分配线程下载
                    dispatcher(restartable);
                    // 循环打印进度
                    printDownloadProgress();
                } catch (IOException e) {
                    System.err.println("Error: 创建文件失败[" + e.getMessage() + "]");
                }
            }
        });
    }

    public void stop() {
        canceled.set(true);
    }

    /**
     * 分配器，决定每个线程下载哪个区间的数据
     * @param breakpoint
     */
    private void dispatcher(boolean breakpoint) {
        long blockSize = fileSize / threadCount; // 每个线程要下载的数据量
        long lowerBound, upperBound;
        int threadID;
        Logger.MyProp prop = logger.getProp();
        for (int i = 0; i < threadCount; i++) {
            if (breakpoint) {
                threadID = i;
                lowerBound = prop.getThreadInfo().get("thread-" + i);
            } else {
                threadID = i;
                lowerBound = i * blockSize;
            }
            upperBound = (i == threadCount - 1) ? fileSize - 1 : lowerBound + blockSize;
            THREAD_POOL_EXECUTOR.execute(new DownloadTask(url, lowerBound, upperBound, file, canceled, threadID, lock));
        }
    }

    /**
     * 循环打印进度，直到下载完毕，或任务被取消
     */
    private void printDownloadProgress() {
        long downloadedSize = file.getWroteSize();
        int i = 0;
        long lastSize = 0; // 三秒前的下载量
        while (!canceled.get() && downloadedSize < fileSize) {
            if (i++ % 4 == 3) { // 每3秒打印一次
                System.out.printf("下载进度：%.2f%%, 已下载：%.2fMB，当前速度：%.2fMB/s\n",
                        downloadedSize / (double) fileSize * 100,
                        downloadedSize / 1024.0 / 1024,
                        (downloadedSize - lastSize) / 1024.0 / 1024 / 3);
                lastSize = downloadedSize;
                i = 0;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
            downloadedSize = file.getWroteSize();
        }
        file.close();
        if (canceled.get()) {
            try {

            } catch (Exception ignore) {
            }
            System.err.println("x 下载失败，任务已取消");
        } else {
            System.out.println("* 下载成功，本次用时" + (System.currentTimeMillis() - beginTime) / 1000 + "秒");
        }
    }

    /**
     * @return 要下载的文件的尺寸
     */
    private long getFileSize() {
        if (fileSize != 0) {
            return fileSize;
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("HEAD");
            conn.connect();
            System.out.println("* 连接服务器成功");
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL错误");
        } catch (IOException e) {
            System.err.println("x 连接服务器失败[" + e.getMessage() + "]");
            return -1;
        }
        return conn.getContentLength();
    }
}
