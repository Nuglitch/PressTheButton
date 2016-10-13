package com.merce.oscar.pressthebutton;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

        TextView title = (TextView) findViewById(R.id.app_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MAXWELL_BOLD.ttf");
        title.setTypeface(font);

        TextView startButton = (TextView) findViewById(R.id.start_button);
        font = Typeface.createFromAsset(getAssets(), "fonts/MAXWELL_REGULAR.ttf");
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
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ((TextView) findViewById(R.id.game_score)).setText(data.getStringExtra("game_score"));
            }
        }
    }
}
