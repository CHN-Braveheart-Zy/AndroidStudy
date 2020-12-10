package com.zjp.androidstudy.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.util.Log;

import com.zjp.androidstudy.DownloadActivity;
import com.zjp.androidstudy.R;
import com.zjp.androidstudy.utils.AppContext;
import com.zjp.androidstudy.utils.AudioRecordUtils;
import com.zjp.androidstudy.utils.AudioTrackUtils;
import com.zjp.androidstudy.utils.ToastUtils;

import java.io.IOException;

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/12/02
 *     version: 1.0
 *     desc   :
 * </pre>
 */
public class MainViewModel {
    private static final String TAG = "MainViewModel";
    Context context = AppContext.getContext();
    private AudioRecordUtils utils = new AudioRecordUtils();
    AudioTrackUtils playUtils = new AudioTrackUtils();
    private String path;
    private String cache2;
    private MediaRecorder recorder;

    public MainViewModel() {
        cache2 = context.getCacheDir() + "/Cache2";
    }

    public void startRecording() {
        ToastUtils.show("开始录音");
        path = context.getCacheDir() + "/AudioRecord.pcm";
        utils.startRecording(path);

    }


    public void stopRecording() {
        ToastUtils.show("停止录音");
        utils.stopRecording();
    }

    public void player() {
        ToastUtils.show("播放");

        playUtils.player(path);
    }

    public void mediaPlayer() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)  //媒体
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) //播放类型
                .build();
        SoundPool build = new SoundPool.Builder().setMaxStreams(10).setAudioAttributes(attributes).build();
        int load = build.load(context, R.raw.voice_testing_dmeo, 1);
        build.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                build.play(sampleId, 1, 1, 0, 0, 1);
            }
        });
        Log.d(TAG, "mediaPlayer: load:" + load);
    }

    public void mediaRecord() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(cache2);
            recorder.prepare();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Log.d(TAG, "onError: ");
                }
            });
            recorder.start();
        } catch (Exception e) {
            Log.e(TAG, "mediaRecord: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mediaRecordStop() {
        try {
            recorder.stop();
        } catch (Exception e) {
            Log.e(TAG, "mediaRecordStop: " + e.getMessage());
        }
    }

    public void startDownload() {
        context.startActivity(new Intent(context, DownloadActivity.class));
    }
}
