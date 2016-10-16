package com.merce.oscar.pressthebutton;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Oscar Merce.
 */
abstract class ElementView extends View {

    protected GameActivity game;

    public ElementView(Context context) {
        super(context);
        this.init(context);
    }

    public ElementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ElementView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context);
    }

    public void setGame(GameActivity game) {
        this.game = game;
    }

    abstract void init(Context context);

    abstract void timeAlert(int time);
}
