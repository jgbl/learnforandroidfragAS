package org.de.jmg.lib;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by hmnatalie on 13.10.15.
 */
public class ScrollGestureListener implements   GestureDetector.OnGestureListener
{

    public TextView t;
    public OnTouchListenerScroll l;

    public ScrollGestureListener(TextView t, OnTouchListenerScroll l)
    {
        super();
        this.t = t;
        this.l = l;
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        try
        {
            if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP)
            {
                t.getParent().requestDisallowInterceptTouchEvent(false);
                if (l.oldMovementMethod!=null) t.setMovementMethod(l.oldMovementMethod);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //lib.ShowException(_main, ex);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {


    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if (e1 ==null||e2==null)return false;
        try
        {
            t.getParent().requestDisallowInterceptTouchEvent(true);
            IBorderedView.BottomOrTop pos = ((IBorderedView)t).getScrollBottomOrTopReached();
            float distY = e2.getY()-e1.getY();
            float distX = e2.getX()-e1.getX();
            if ((Math.abs(distX) > Math.abs(distY)) || (pos == IBorderedView.BottomOrTop.both) || (pos == IBorderedView.BottomOrTop.top
                    && distY >= 0)
                    || (pos == BorderedTextView.BottomOrTop.bottom
                    && distY <= 0))
            {
                t.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //lib.ShowException(_main, ex);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {


    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {

        return false;
    }

}
