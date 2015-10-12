/*
 * Original at https://code.google.com/p/android-file-chooser/
 *
 * Modified by J.M.Goebel
 *
 * License:
 * http://www.gnu.org/licenses/gpl.html
 *
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
package br.com.thinkti.android.filechooserfrag;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.de.jmg.learn.R;

import java.util.List;

//import br.com.thinkti.android.filechooserfrag.R;

public class QuizletArrayAdapter extends ArrayAdapter<RowData> {

	private Context c;
	private int id;
	private List<RowData> items;
	private fragFileChooserQuizlet parent;

	public QuizletArrayAdapter(Context context, int textViewResourceId,
							   List<RowData> objects,fragFileChooserQuizlet parent) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
		this.parent = parent;
	}

	public RowData getItem(int i)
	{
		return items.get(i);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(id, null);
		}
		final RowData o = items.get(position);
		if (o != null) {
			ImageView im = (ImageView) v.findViewById(R.id.img1);
			TextView t1 = (TextView) v.findViewById(R.id.TextView01);
			TextView t2 = (TextView) v.findViewById(R.id.TextView02);

			/*if(o.getData().equalsIgnoreCase(getString(R.string.folder))){
				im.setImageResource(R.drawable.folder);
			} else if (o.getData().equalsIgnoreCase(getString(R.string.parentDirectory))) {
				im.setImageResource(R.drawable.back);
			} else {
				String name = o.getName().toLowerCase();
				if (name.endsWith(".xls") ||  name.endsWith(".xlsx"))
					im.setImageResource(R.drawable.xls);
				else if (name.endsWith(".doc") ||  name.endsWith(".docx"))
					im.setImageResource(R.drawable.doc);
				else if (name.endsWith(".ppt") ||  o.getName().endsWith(".pptx"))
					im.setImageResource(R.drawable.ppt);
				else if (name.endsWith(".pdf"))
					im.setImageResource(R.drawable.pdf_list);
				else if (name.endsWith(".apk"))
					im.setImageResource(R.drawable.and);
				else if (name.endsWith(".txt"))
					im.setImageResource(R.drawable.txt);
				else if (name.endsWith(".jpg") || name.endsWith(".jpeg"))
					im.setImageResource(R.drawable.jpg);
				else if (name.endsWith(".png"))
					im.setImageResource(R.drawable.png);
				else if (name.endsWith(".zip"))
					im.setImageResource(R.drawable.zip);
				else if (name.endsWith(".rtf"))
					im.setImageResource(R.drawable.rtf);
				else if (name.endsWith(".gif"))
					im.setImageResource(R.drawable.gif);
				else*/
					im.setImageResource(R.drawable.whitepage);
			//}

			if (t1 != null)
				t1.setText(o.name + "(" + o.numCards + ")");
			if (t2 != null)
				t2.setText(o.description);

		}
		if (position == this.getCount()-1 && this.parent.getTotalPages() > this.parent.getPage()) this.parent.nextPage();
		return v;

	}
	/**
	 * Return a localized string from the application's package's
	 * default string table.
	 *
	 * @param resId Resource id for the string
	 */
	public final String getString(@StringRes int resId) {
		return c.getResources().getString(resId);
	}

	/**
	 * Return a localized formatted string from the application's package's
	 * default string table, substituting the format arguments as defined in
	 * {@link java.util.Formatter} and {@link String#format}.
	 *
	 * @param resId Resource id for the format string
	 * @param formatArgs The format arguments that will be used for substitution.
	 */

	public final String getString(@StringRes int resId, Object... formatArgs) {
		return c.getResources().getString(resId, formatArgs);
	}


}
