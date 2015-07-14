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
import android.widget.ArrayAdapter;

public class AbstractScaledArrayAdapter<T> extends ArrayAdapter<T> {

	public AbstractScaledArrayAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}

	public float Scale = 1.0f;
	
	@Override
	public void addAll(Collection<? extends T>collection)
	{
		for (T s: collection)
		{
			this.add(s);
		}
	}
	
	@Override
	public int getCount()
	{
		int count = super.getCount();
		return count;
	}

}
