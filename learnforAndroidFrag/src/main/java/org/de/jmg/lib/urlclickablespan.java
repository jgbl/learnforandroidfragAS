package org.de.jmg.lib;

import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by hmnatalie on 14.01.16.
 */
public abstract class urlclickablespan extends ClickableSpan
{
    public String url;
    public urlclickablespan(String url)
    {
        this.url = url;
    }

}
