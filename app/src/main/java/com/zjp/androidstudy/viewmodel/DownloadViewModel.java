package com.zjp.androidstudy.viewmodel;

import android.content.Context;

import com.zjp.androidstudy.utils.AppContext;
import com.zjp.androidstudy.utils.download.Downloader;

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/12/10
 *     version: 1.0
 *     desc   :
 * </pre>
 */
public class DownloadViewModel {
    Downloader downloader;
    Context context;

    public DownloadViewModel() {
        this.context = AppContext.getContext();
        String url = "https://oss.zhigujinyun.com/video/zhonghenglong_introduce.mp4";
        String path = context.getExternalCacheDir() + "/zhigujinyun.mp4";

        downloader = new Downloader(url, 4, path);
    }

    public void start() {
        downloader.start();
    }

    public void stop() {
        downloader.stop();
    }
}
