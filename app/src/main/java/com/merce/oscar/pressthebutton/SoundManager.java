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
    MediaPlayer bm;
    MediaPlayer mm;
    int maxStreams = 2;
    int soundIds[];
    Context c;
    boolean musicOn = true;
    boolean soundEffectsOn = true;



    protected SoundManager() { }

    public void init(Context context) {
        c = context;
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

    }


    public static SoundManager getInstance() {
        if(instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playTabEffect() {
        if (soundEffectsOn) {
            soundEffects.play(soundIds[0], 1, 1, 2, 0, 1.0f);
        }
    }

    public void playEndEffect() {
        if (soundEffectsOn) {
            soundEffects.play(soundIds[1], 1, 1, 2, 0, 1.0f);
        }
    }

    public void playBackgroundMusic() {
        if (musicOn) {
            if (bm == null || !bm.isPlaying()) {
                bm = MediaPlayer.create(c, R.raw.electronic_synth);
                bm.setLooping(true);
                playMediaPlayer(bm, 100);
            }
        }
    }

    public void stopBackgroundMusic() {
        bm.stop();
    }

    public void playMenuMusic() {
        if (musicOn) {
            if (mm == null || !mm.isPlaying()) {
                mm = MediaPlayer.create(c, R.raw.music_loop_80s);
                mm.setLooping(true);
                playMediaPlayer(mm, 500);
            }
        }
    }

    public void stopMenuMusic() {
        mm.stop();
    }

    private void playMediaPlayer(final MediaPlayer mp, final long waitTime) {
        Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                mp.start();
            }
        };
        mHandler.postDelayed(mRunnable, waitTime);
    }

    private void stopMusic() {
        stopBackgroundMusic();
        stopMenuMusic();
    }

    public void releaseSounds() {
        soundEffects.release();
        stopMusic();
    }
}
