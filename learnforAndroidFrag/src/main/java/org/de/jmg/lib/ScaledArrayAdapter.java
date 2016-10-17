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

import java.util.Collection;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScaledArrayAdapter<T> extends AbstractScaledArrayAdapter<T> {

	public ScaledArrayAdapter(Context context, int resource) {
		super(context, resource);

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		boolean blnNew = (convertView == null);
		View V = super.getDropDownView(position, convertView, parent);
		if (V != null)
		{
			if (V.getTag() == null)
				blnNew = true;
			if (blnNew) {
				resizeviews(V);
			}
		}
		
		return V;
	}

	@Override
	public int getCount()
	{
		int count = super.getCount();
		return count;
	}
	
	@Override
	public void addAll(Collection<? extends T>collection)
	{
		super.addAll(collection);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean blnNew = (convertView == null);
		View V = null;
		int count = super.getCount();
		if(position<0 && count>0) position = 0;
		if (position>=0)
		{
			V = super.getView(position, convertView, parent);
		}
		else
		{
			V = convertView;
		}
		if (V != null)
		{
			if (V.getTag() == null)
				blnNew = true;
			if (blnNew) {
				resizeviews(V);
			}
		}
		
		return V;
	}

	private void resizeviews(View V) {
		if (!(V instanceof TextView)) {
			if (V instanceof ViewGroup) {
				ViewGroup views = (ViewGroup) V;
				for (int i = 0; i < views.getChildCount(); i++) {
					View v = views.getChildAt(i);
					if (v instanceof TextView) {
						TextView t = (TextView) v;
						t.setTextSize(TypedValue.COMPLEX_UNIT_PX,
								t.getTextSize() * Scale);
					}
				}

			}
		} else {
			TextView t = (TextView) V;
			t.setTextSize(TypedValue.COMPLEX_UNIT_PX, t.getTextSize() * Scale);
		}
		if (Scale != 1)
			V.setTag(true);
	}

	public static ScaledArrayAdapter<CharSequence> createFromResource(
			Context context, int textArrayResId, int textViewResId) {
		ArrayAdapter<CharSequence> A = ArrayAdapter.createFromResource(context,
				textArrayResId, textViewResId);
		ScaledArrayAdapter<CharSequence> SA = new ScaledArrayAdapter<CharSequence>(
				context, textViewResId);
		for (int i = 0; i < A.getCount(); i++) {
			SA.add(A.getItem(i));
		}
		return SA;
	}

	{

	}


}
