package com.merce.oscar.pressthebutton;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Oscar Merce.
 */
public class GameActivity extends AppCompatActivity {

    private int pressCount = 0; //counts the number of clicks to the button
    private ArrayList<ElementView> tokens = new ArrayList<>();
    private Handler mHandler;
    private Runnable mRunnable;
    private int counterTime;
    private ArrayList<ElementView> pendingQueue = new ArrayList<>();
    private double acceleration = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        setVolumeControlStream(AudioManager.STREAM_MUSIC); //is able to change the volume


        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MAXWELL_REGULAR.ttf");
        ((TextView) findViewById(R.id.end_game_text)).setTypeface(font);

        if (Config.FIRST_TIME) { //let's see the tutorial
            Config.FIRST_TIME = false;
            ((TextView) findViewById(R.id.instruction_text)).setTypeface(font);
            ((TextView) findViewById(R.id.end_tutorial_text)).setTypeface(font);

            Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_anim);
            findViewById(R.id.button_tutorial).startAnimation(a);

            findViewById(R.id.button_tutorial).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initGame(); //start game
                }
            });
        } else {
            initGame(); //start game
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //start sounds
        SoundManager.getInstance().playBackgroundMusic();
        //resume the game
        if (counterTime > 0) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, calculateVelocity());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop sounds
        SoundManager sm = SoundManager.getInstance();
        sm.stopBackgroundMusic();
        sm.stopPlayEffect();
        //pause the game
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    public void initGame() {
        try {
            findViewById(R.id.tutorial_frame).setVisibility(View.GONE);
            initTimer();  //initialize timer function
            pressCount = 0;
            restartGame();  //init the game
        } catch (Exception e) {

        }
    }

    private void initTimer() {
        mHandler = new Handler();
        counterTime = Config.TOTAL_TIME;
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (counterTime == 0) {
                    //you lost!
                    endGame();
                } else {
                    //the game goes on
                    --counterTime;
                    timerAlert();
                    mHandler.postDelayed(mRunnable, calculateVelocity());
                }
            }
        };
    }

    private void restartGame() {
        addNewElement();
        //register pending task elements
        for (ElementView elem : tokens) {
            registerElementAsPending(elem);
            if (elem instanceof CircleView) {
                ((CircleView) elem).redraw();
            }
        }
        //restart timer
        setTimeEvent();
    }

    private void endGame() {
        Intent intent = new Intent();
        intent.putExtra("game_score", pressCount + "");
        setResult(RESULT_OK, intent);
        removeClickListeners();

        TextView endGameText = (TextView) findViewById(R.id.end_game_text);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.end_game_anim);
        endGameText.startAnimation(a);
        endGameText.setVisibility(View.VISIBLE);

        SoundManager sm = SoundManager.getInstance();
        sm.playEndEffect();
        Handler mHandler = new Handler();
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        mHandler.postDelayed(mRunnable, Math.max(a.getDuration(), sm.getEndEffectDuration()));
    }

    private void timerAlert() {
        //warning every token
        for (ElementView t : tokens) {
            t.timeAlert(counterTime);
        }
    }

    public void setTimeEvent() {
        try {
            counterTime = Config.TOTAL_TIME;
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, calculateVelocity());
        } catch (Exception e) {
            Log.e("TIMER", "Error in setTimeEvent function");
        }
    }

    private void registerElementAsPending(ElementView e) {
        if (e instanceof PendingTask && !pendingQueue.contains(e)) {
            pendingQueue.add(e);
        }
    }

    public void taskComplete(ElementView e) {
        if (e instanceof PendingTask && pendingQueue.contains(e)) {
            pendingQueue.remove(e);
        }
        if (pendingQueue.isEmpty()) {
            ++pressCount;
            ((TextView) findViewById(R.id.press_count)).setText(pressCount + ""); //update the counter view
            restartGame();
        }
    }

    private void addNewElement() {
        CircleView elem = new CircleView(this);
        elem.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        elem.setGame(this);
        FrameLayout buttonContainer = (FrameLayout) findViewById(R.id.button_container);
        buttonContainer.addView(elem);
        tokens.add(elem);

    }

    private long calculateVelocity() {
       return (long) (((pressCount / 10) + 1) * acceleration * Config.VELOCITY);
    }

    private void removeClickListeners() {
        for (ElementView t : tokens) {
            t.setClickable(false); //remove on click events
        }
    }
}
