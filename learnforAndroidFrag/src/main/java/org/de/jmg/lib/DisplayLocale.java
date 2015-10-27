package org.de.jmg.lib;

import java.util.Locale;

public class DisplayLocale
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

}
