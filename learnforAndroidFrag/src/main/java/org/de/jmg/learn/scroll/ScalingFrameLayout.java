package org.de.jmg.learn.scroll;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.FrameLayout;

public class ScalingFrameLayout extends FrameLayout {

    private float scale = 1;

    public ScalingFrameLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public void setScale(float factor){
        scale = factor;
        invalidate();
    }
    public float getScale(){
        return scale;
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.scale(scale, scale);
        super.onDraw(canvas);
    }

}