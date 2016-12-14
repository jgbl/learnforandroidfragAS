package org.de.jmg.lib;

import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by hmnatalie on 14.01.16.
 */
public abstract class urlclickablespan extends ClickableSpan
{
    public String url = null;
    public String originalURL = null;

    public urlclickablespan(String url)
    {
        this.url = url;
    }

    public urlclickablespan(String url, String originalURL)
    {
        this.url = url;
        if (!originalURL.equalsIgnoreCase(url))
        {
            this.originalURL = originalURL;
        }
    }
}
