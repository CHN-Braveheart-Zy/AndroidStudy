package com.zjp.androidstudy.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/12/11
 *     version: 1.0
 *     desc   :
 * </pre>
 */
public class PrintDirPath {
    public static void printAndroidFile() {
        Context context = AppContext.getContext();
        File dataDirEnv = Environment.getDataDirectory();
        System.out.println("Environment.getDataDirectory()=                    path: " + dataDirEnv.getAbsolutePath());
        System.out.println("Environment.getDataDirectory()=           absolute path: " + dataDirEnv.getAbsolutePath());
        File downloadCacheDir = Environment.getDownloadCacheDirectory();
        System.out.println("Environment.getDownloadCacheDirectory()=           path: " + downloadCacheDir.getPath());
        System.out.println("Environment.getDownloadCacheDirectory()= absolute  path: " + downloadCacheDir.getAbsolutePath());
        File rootDir = Environment.getRootDirectory();
        System.out.println("Environment.getRootDirectory()=                    path: " + rootDir.getPath());
        System.out.println("Environment.getRootDirectory()=           absolute path: " + rootDir.getAbsolutePath());
        File storageDir = Environment.getStorageDirectory();
        System.out.println("Environment.getStorageDirectory()=                 path: " + storageDir.getPath());
        System.out.println("Environment.getStorageDirectory()=        absolute path: " + storageDir.getAbsolutePath());

        String storageState = Environment.getExternalStorageState();
        System.out.println("Environment.getExternalStorageState()= storageState : " + storageState);

        File cacheDir = context.getCacheDir();
        System.out.println("context.getCacheDir()=                             path: " + cacheDir.getPath());
        System.out.println("context.getCacheDir()=                    absolute path: " + cacheDir.getAbsolutePath());
        File dataDir = context.getDataDir();
        System.out.println("context.getDataDir()=                              path: " + dataDir.getPath());
        System.out.println("context.getDataDir()=                     absolute path: " + dataDir.getAbsolutePath());
        File codeCacheDir = context.getCodeCacheDir();
        System.out.println("context.getCodeCacheDir()=                         path: " + codeCacheDir.getPath());
        System.out.println("context.getCodeCacheDir()=                absolute path: " + codeCacheDir.getAbsolutePath());
        File filesDir = context.getFilesDir();
        System.out.println("context.getFilesDir()=                             path: " + filesDir.getPath());
        System.out.println("context.getFilesDir()=                    absolute path: " + filesDir.getAbsolutePath());
        File obbDir = context.getObbDir();
        System.out.println("context.getObbDir()=                               path: " + obbDir.getPath());
        System.out.println("context.getObbDir()=                      absolute path: " + obbDir.getAbsolutePath());
        File externalCacheDir = context.getExternalCacheDir();
        System.out.println("context.getExternalCacheDir()=                     path: " + externalCacheDir.getPath());
        System.out.println("context.getExternalCacheDir()=            absolute path: " + externalCacheDir.getAbsolutePath());
        File noBackupFilesDir = context.getNoBackupFilesDir();
        System.out.println("context.getNoBackupFilesDir()=                     path: " + noBackupFilesDir.getPath());
        System.out.println("context.getNoBackupFilesDir()=            absolute path: " + noBackupFilesDir.getAbsolutePath());
        File[] externalCacheDirs = context.getExternalCacheDirs();
        for (File file : externalCacheDirs) {
            System.out.println("context.getExternalCacheDirs()=                path: " + file.getPath());
            System.out.println("context.getExternalCacheDirs()=       absolute path: " + file.getAbsolutePath());
            System.out.println();
        }
        File[] externalMediaDirs = context.getExternalMediaDirs();
        for (File file : externalMediaDirs) {
            System.out.println("context.getExternalMediaDirs()=                path: " + file.getPath());
            System.out.println("context.getExternalMediaDirs()=       absolute path: " + file.getAbsolutePath());
            System.out.println();
        }
    }
}
