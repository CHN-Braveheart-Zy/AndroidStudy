package com.zjp.commonutils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/12/02
 *     version: 1.0
 *     desc   : 录音播放
 * </pre>
 */
public class AudioTrackUtils {
    private static final String TAG = "AudioTrackUtils";
    private AudioTrack audioTrack;
    private PlayerThread playerThread;

    public AudioTrackUtils() {
        createAudioTrack();
    }

    private void createAudioTrack() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        AudioFormat audioFormat = new AudioFormat.Builder()
                .setSampleRate(AudioRecordUtils.Config.SIMPLE_RATE_44100)
                .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build();
        audioTrack = new AudioTrack(attributes, audioFormat, AudioRecordUtils.Config.BUFFER_SIZE,
                AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
    }

    /**
     * 0.0 - 1.0
     * @param volume
     */
    public void setVolume(float volume) {
        float ret = Math.max(0.0f,volume);
        ret = Math.min(1.0f, volume);
        audioTrack.setVolume(ret);
    }

    public void player(String cachePath) {
        if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            audioTrack.stop();
        }
        if (playerThread != null) {
            playerThread.stopPlayer();
        }
        audioTrack.play();
        playerThread = new PlayerThread(this);
        playerThread.player(cachePath);
    }

    public static class PlayerThread extends Thread {
        WeakReference<AudioTrackUtils> reference;
        String path;
        private boolean isPlaying;

        public PlayerThread(AudioTrackUtils utils) {
            reference = new WeakReference<>(utils);
        }

        @Override
        public void run() {
            super.run();
            if (reference.get() == null) return;
            byte[] buffer = new byte[AudioRecordUtils.Config.BUFFER_SIZE];
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(path);
                while (isPlaying && fis.available() > 0 && reference.get() != null) {
                    Log.d(TAG, "PlayerThread#run: 循环读取");
                    int readCount = fis.read(buffer);
                    if (readCount > 0) {
                        int writeRet = reference.get().audioTrack.write(buffer, 0, readCount);
                        Log.d(TAG, "PlayerThread#run: 写入队列结果: " + writeRet);
                    }
                }
            } catch (Exception e) {
                stopPlayer();
                Log.e(TAG, "PlayerThread#run: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void player(String path) {
            this.path = path;
            isPlaying = true;
            start();
        }

        public void stopPlayer() {
            if (!isPlaying) return;
            isPlaying = false;
            reference.get().audioTrack.stop();
        }
    }
}
