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

import java.io.File;

import org.de.jmg.learn.R;
import org.de.jmg.lib.lib.Sounds;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SoundsArrayAdapter extends
		AbstractScaledArrayAdapter<SoundSetting> {

	private Activity _Activity;

	public SoundsArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		if (lib.AssetSounds[0] == null)
			lib.initSounds();
		_Activity = (Activity) context;
		SharedPreferences prefs = _Activity
				.getPreferences(Context.MODE_PRIVATE);
		for (int i = 0; i < lib.Sounds.values().length; i++) {
			Sounds SoundItem = Sounds.values()[i];
			String Name = _Activity.getResources().getStringArray(
					R.array.spnSounds)[i];
			String defValue = "";
			defValue = lib.AssetSounds[SoundItem.ordinal()];
			String Sound = prefs.getString(SoundItem.name(), defValue);
			this.add(new SoundSetting(SoundItem, Name, Sound));
		}

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		boolean blnNew = false;
		LayoutInflater inflater = _Activity.getLayoutInflater();
		SoundSetting SoundItem; 
		if (position < 0) 
		{
			return convertView;
			//SoundItem = new SoundSetting(Sounds.Falsch0, "Test", "Test");
		}
		else 
		{
			SoundItem = getItem(position);
		}
		
		View row;

		if (convertView == null) {
			row = inflater.inflate(R.layout.soundsspinnerrow, parent, false);
			blnNew = true;
		} else {
			row = convertView;
			if (row.getTag() == null)
				blnNew = true;
		}

		TextView label = (TextView) row.findViewById(R.id.txtSounds1);
		if (blnNew)
			label.setTextSize(TypedValue.COMPLEX_UNIT_PX, label.getTextSize()
					* Scale);
		label.setText(SoundItem.SoundName);

		TextView label2 = (TextView) row.findViewById(R.id.txtSounds2);
		File F = new File(SoundItem.SoundPath);
		if (blnNew)
			label2.setTextSize(TypedValue.COMPLEX_UNIT_PX, label2.getTextSize()
					* Scale);
		label2.setText(F.getName().substring(0,
				(F.getName().length() > 25) ? 25 : F.getName().length()));
		if (Scale != 1.0f)
			row.setTag(true);
		return row;
	}

}