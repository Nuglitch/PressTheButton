package com.merce.oscar.pressthebutton;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;

/**
 * Created by PC-O on 16/10/2016.
 */

public class SoundManager {
    private static SoundManager instance = null;
    SoundPool soundEffects;
    int maxMusics = 2;
    MediaPlayer mp[];
    int maxStreams = 2;
    int soundIds[];
    boolean musicOn;



    protected SoundManager() { }

    public void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundEffects = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .setAudioAttributes(attrs)
                    .build();
        } else {
            soundEffects = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }
        soundIds = new int[maxStreams];
        soundIds[0] = soundEffects.load(context, R.raw.bleep_sound, 1);
        soundIds[1] = soundEffects.load(context, R.raw.error, 1);

        mp = new MediaPlayer[maxMusics];
        mp[0] = MediaPlayer.create(context, R.raw.electronic_synth);
        mp[0].setLooping(true);
        mp[1] = MediaPlayer.create(context, R.raw.music_loop_80s);
        mp[1].setLooping(true);

    }


    public static SoundManager getInstance() {
        if(instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playTabEffect() {
        if (musicOn) {
            soundEffects.play(soundIds[0], 1, 1, 2, 0, 1.0f);
        }
    }

    public void playEndEffect() {
        if (musicOn) {
            soundEffects.play(soundIds[1], 1, 1, 2, 0, 1.0f);
        }
    }

    public void playBackgroundMusic() {
        playMediaPlayer(0, 500);
    }

    public void stopBackgroundMusic() {
        stopMediaPlayer(0);
    }

    public void playMenuMusic() {
        playMediaPlayer(1, 500);
    }

    public void stopMenuMusic() {
        stopMediaPlayer(1);
    }

    private void playMediaPlayer(final int musicId, final long waitTime) {
        if (musicOn) {
            if (!mp[musicId].isPlaying()) {
                Handler mHandler = new Handler();
                Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mp[musicId].start();
                    }
                };
                mHandler.postDelayed(mRunnable, waitTime);
            }
        }
    }

    private void stopMediaPlayer(int musicId) {
        if (mp[musicId].isPlaying()) {
            try {
                mp[musicId].stop();
                mp[musicId].prepare();
            } catch (Exception e) {}
        }
    }

    private void stopMusic() {
        stopBackgroundMusic();
        stopMenuMusic();
    }

    public void releaseSounds() {
        soundEffects.release();
        stopMusic();
    }

    public void changeMusicState() {
        musicOn = !musicOn;
        if (musicOn) {
            playMenuMusic();
        } else {
            stopMenuMusic();
        }
    }
}
