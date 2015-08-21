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

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Spinner;

public class NoClickSpinner extends Spinner {
	public boolean blnDontCallOnClick;

	public NoClickSpinner(Context context) {
		super(context);

	}

	public NoClickSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public NoClickSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);


	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (!blnDontCallOnClick)
			super.onClick(dialog, which);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!blnDontCallOnClick) {
			return super.onTouchEvent(event);
		} else {
			return true;
		}

	}
	
	@Override
	public void onLayout(boolean changed, int l,int t, int r, int b) 
	{
		try
		{
			super.onLayout(changed, l, t, r, b);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
