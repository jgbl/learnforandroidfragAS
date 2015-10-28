package org.de.jmg.lib;

import org.de.jmg.learn.MainActivity;
import org.de.jmg.learn.R;

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
            if (locale.toString().equalsIgnoreCase("_off"))
            {
                if (lib.main != null)
                {
                    return lib.main.getString(R.string.off);
                }
                else
                {
                    return "off";
                }
            }
            else
            {
                return locale.getDisplayLanguage() + " "
                        + locale.getDisplayCountry() + " "
                        + locale.getDisplayVariant() + "("
                        + locale.toString() + ")";
            }
        }
        else {
            return super.toString();
        }
    }

    @Override
    public int compareTo(DisplayLocale another) {
        int res = this.locale.getDisplayLanguage().compareTo(another.locale.getDisplayLanguage());
        if (res == 0) res = this.locale.getDisplayCountry().compareTo(another.locale.getDisplayCountry());
        if (res == 0) res = this.locale.toString().compareTo(another.locale.toString());
        return res;
    }
}
