package org.de.jmg.lib;

import android.annotation.SuppressLint;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class OnTouchListenerScroll implements View.OnTouchListener
{
    private List<RemoveCallbackListener> listeners = new ArrayList<>();

    public MovementMethod oldMovementMethod;
    public GestureDetector detector;

    public OnTouchListenerScroll( GestureDetector detector, RemoveCallbackListener removeCallbacks)
    {
        super();
        this.detector = detector;
        this.addListener(removeCallbacks);
    }


    public void addListener(RemoveCallbackListener toAdd)
    {
        listeners.add(toAdd);
    }



    public boolean onTouch(View v, MotionEvent event)
    {
            //removeCallbacks();
        for (RemoveCallbackListener hl : listeners)
            hl.RemoveCallback();
        try
        {
                TextView t = (TextView) v;
                if (v.getVisibility() == View.VISIBLE && t.getLineCount() > 3) {
                    if (t.getMovementMethod() != ScrollingMovementMethod.getInstance()) {
                        oldMovementMethod = t.getMovementMethod();
                    }
                    t.setMovementMethod(android.text.method.ScrollingMovementMethod.getInstance());
                    t.getParent().requestDisallowInterceptTouchEvent(true);
                    detector.onTouchEvent(event);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        t.getParent().requestDisallowInterceptTouchEvent(false);
                        if (oldMovementMethod != null)
                            t.setMovementMethod(oldMovementMethod);
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //lib.ShowException(_main, ex);
            }

            return false;
        }
    }



