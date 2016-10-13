package com.merce.oscar.pressthebutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Oscar on 09/04/2016.
 */
public class CircleView extends ElementView implements View.OnClickListener, PendingTask {
    private Paint paintCircle;
    private Paint paintText;
    private final Rect textBounds = new Rect();
    private int counterTime;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);
    }


    @Override
    public void init(Context context) {
        // create the Paint and set its color
        paintCircle = new Paint();
        paintCircle.setColor(Color.RED);

        paintText = new Paint();
        paintText.setColor(Color.WHITE);

        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(100, 100, 100, paintCircle);
        drawTextCentred(canvas, counterTime + "", 100, 100, paintText);
    }

    public void setRandomColor() {
        //get a random color to paint the button
        //max index is 230 because we don't want colors close to white color (the number in the center of the button is white)
        int c = Color.argb(255, Utils.getRandomNumber(0, 230), Utils.getRandomNumber(0, 230), Utils.getRandomNumber(0, 230));
        paintCircle.setColor(c);
        invalidate();
    }

    public void drawTextCentred(Canvas canvas, String text, float cx, float cy, Paint paint){
        paint.setTextSize(Utils.getGoodTextSize(text));
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint);
    }

    @Override
    public void onClick(View v) {
        Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE); //do a little vibration
        vibe.vibrate(50);

        this.setVisibility(View.INVISIBLE);

        game.taskComplete(this);
    }

    public void redraw() {
        this.setRandomColor();
        counterTime = Config.totalTime;

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                //now we can retrieve the width and height
                int width = CircleView.this.getWidth();
                int height = CircleView.this.getHeight();

                FrameLayout buttonContainer = (FrameLayout) game.findViewById(R.id.button_container);
                int screenHeight = buttonContainer.getHeight() - height;
                int screenWidth = buttonContainer.getWidth() - width;
                if (screenHeight > 0 && screenWidth > 0) {
                    //update the button position
                    CircleView.this.setY(Utils.getRandomNumber(0, screenHeight));
                    CircleView.this.setX(Utils.getRandomNumber(0, screenWidth));
                }
                CircleView.this.setVisibility(View.VISIBLE);

                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    CircleView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    CircleView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


    }

    @Override
    public void timeAlert(int time) {
        counterTime = time;
        invalidate();
    }

}
