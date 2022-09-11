package com.ricci.tt;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface Scene {
    void draw(Canvas canvas);
    boolean onTouchEvent(MotionEvent event);
    void update();
}
