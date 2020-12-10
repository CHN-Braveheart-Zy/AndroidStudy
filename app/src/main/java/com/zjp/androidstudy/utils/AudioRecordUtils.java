package com.zjp.androidstudy.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/12/01
 *     version: 1.0
 *     desc   :
 * </pre>
 */
public class AudioRecordUtils {
    private static final String TAG = "AudioRecordUtils";
    //录音类
    private AudioRecord audioRecord;
    //结果监听
    private RecodingListener mListener;
    private RecodingThread recodingThread;

    public AudioRecordUtils() {
        initAudioRecord();
    }

    private void initAudioRecord() {
        int minBufferSize = AudioRecord.getMinBufferSize(Config.SIMPLE_RATE_44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, Config.SIMPLE_RATE_44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
    }


    public void startRecording(String cachePath) {
        prepare();
        audioRecord.startRecording();
        recodingThread = new RecodingThread(this);
        recodingThread.startRecording(cachePath);
    }

    private void prepare() {
        if (audioRecord != null && audioRecord.getState() != AudioRecord.STATE_UNINITIALIZED) {
            audioRecord.stop();
        }

        if (recodingThread != null) {
            recodingThread.stopRecording();
        }
    }

    public void stopRecording() {
        recodingThread.stopRecording();
    }

    public static class RecodingThread extends Thread {
        WeakReference<AudioRecordUtils> referenceUtils;
        private boolean isRecoding;
        private String cachePath;

        public RecodingThread(AudioRecordUtils utils) {
            referenceUtils = new WeakReference<>(utils);
        }

        @Override
        public void run() {
            if (referenceUtils.get() == null) return;
            FileOutputStream fos = null;
            try {
                referenceUtils.get().audioRecord.startRecording();
                fos = new FileOutputStream(cachePath);
                byte[] buffer = new byte[Config.BUFFER_SIZE];
                while (isRecoding && referenceUtils.get() != null) {
                    //从队列读取内容
                    int readCount = referenceUtils.get().audioRecord.read(buffer, 0, Config.BUFFER_SIZE);
                    Log.d(TAG, "run: 循环读取");
                    if (readCount < 0) {
                        continue;
                    }
                    fos.write(buffer, 0, readCount);
                }
                fos.flush();
                if (referenceUtils.get().mListener != null) {
                    referenceUtils.get().mListener.onSuccess(cachePath);
                }
            } catch (Exception e) {
                stopRecording();
                if (referenceUtils.get().mListener != null) {
                    referenceUtils.get().mListener.onFailure(e.getMessage());
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public synchronized void startRecording(String cachePath) {
            this.cachePath = cachePath;
            isRecoding = true;
            start();
        }

        public synchronized void stopRecording() {
            if (!isRecoding) {
                return;
            }
            isRecoding = false;
        }
    }

    public void setRecodingListener(RecodingListener listener) {
        mListener = listener;
    }

    public interface RecodingListener {
        void onSuccess(String cacheFile);

        void onFailure(String msg);
    }

    static class Config {
        public static final int BUFFER_SIZE = 2048;
        public static final int SIMPLE_RATE_44100 = 44100;
    }
}
