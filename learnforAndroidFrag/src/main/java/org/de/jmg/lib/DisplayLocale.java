package org.de.jmg.lib;

import java.util.Locale;

public class DisplayLocale implements Comparable<DisplayLocale>
{
    public Locale locale;
    public DisplayLocale(Locale locale)
    {
        this.locale = locale;
    }
    @Override
    public String toString() {
        if(locale!=null)
        {
            return locale.getDisplayLanguage() + " " + locale.getDisplayCountry();
        }
        else {
            return super.toString();
        }
    }

    @Override
    public int compareTo(DisplayLocale another) {
        int res = this.locale.getDisplayLanguage().compareTo(another.locale.getDisplayLanguage());
        if (res == 0) res = this.locale.getDisplayCountry().compareTo(another.locale.getDisplayCountry());
        return res;
    }
}
