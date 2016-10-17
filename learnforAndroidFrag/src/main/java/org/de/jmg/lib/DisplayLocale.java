/*
 * Copyright (c) 2015 GPL by J.M.Goebel. Distributed under the GNU GPL v3.
 *
 * 08.06.2015
 *
 * This file is part of learnforandroid.
 *
 * learnforandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  learnforandroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.de.jmg.lib;

import android.support.annotation.NonNull;

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
    public int compareTo(@NonNull DisplayLocale another) {
        int res = this.locale.getDisplayLanguage().compareTo(another.locale.getDisplayLanguage());
        if (res == 0) res = this.locale.getDisplayCountry().compareTo(another.locale.getDisplayCountry());
        if (res == 0) res = this.locale.toString().compareTo(another.locale.toString());
        return res;
    }
}
