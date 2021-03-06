package com.merce.oscar.pressthebutton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Oscar Merce.
 */
public class MenuActivity extends AppCompatActivity {
    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

        setVolumeControlStream(AudioManager.STREAM_MUSIC); //is able to change the volume
        SoundManager.getInstance().init(this);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MAXWELL_REGULAR.ttf");
        ((TextView) findViewById(R.id.app_title)).setTypeface(font);

        font = Typeface.createFromAsset(getAssets(), "fonts/MAXWELL_LIGHT.ttf");
        TextView startButton = (TextView) findViewById(R.id.start_button);
        startButton.setTypeface(font);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //do a little vibration
                vibe.vibrate(50);

                Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
                MenuActivity.this.startActivityForResult(myIntent, PICK_CONTACT_REQUEST);
            }
        });

        initConfigurationButtons();
        checkFirstTime();
        displayGameScore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundManager.getInstance().playMenuMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundManager.getInstance().stopMenuMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundManager.getInstance().releaseSounds();
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            displayGameScore();
        }
    }

    private void displayGameScore() {
        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.game_score)).setText(settings.getInt(Config.PREFS_SCORE, 0) + "");
    }

    private void setImageSoundButton() {
        ImageButton soundButton = (ImageButton) findViewById(R.id.sound_button);
        if (SoundManager.getInstance().isMusicOn()) { //sound on
            soundButton.setImageResource(R.drawable.ic_sounds_on);
        } else { //sound off
            soundButton.setImageResource(R.drawable.ic_sound_off);
        }
        soundButton.invalidate();
    }

    private void initConfigurationButtons() {
        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_PRIVATE);
        SoundManager.getInstance().setMusicOn(settings.getBoolean(Config.PREFS_SOUND, true));
        setImageSoundButton();

        ImageButton soundButton = (ImageButton) findViewById(R.id.sound_button);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundManager.getInstance().changeMusicState();
                setImageSoundButton();
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Config.PREFS_SOUND, SoundManager.getInstance().isMusicOn());
        editor.putBoolean(Config.PREFS_FIRST, false);

        // Commit the edits!
        editor.commit();
    }

    private void checkFirstTime() {
        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_PRIVATE);
        Config.FIRST_TIME = settings.getBoolean(Config.PREFS_FIRST, true);
    }
}
