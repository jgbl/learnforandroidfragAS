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

import br.com.thinkti.android.filechooserfrag.Option;

public class Option implements Comparable<Option>{
	private String name;
	private String data;
	private String path;
	private boolean folder;
	private boolean parent;
	private boolean back;
	
	public Option(String n,String d,String p, boolean folder, boolean parent, boolean back)
	{
		name = n;
		data = d;
		path = p;
		this.folder = folder;
		this.parent = parent;
		this.back = back;
	}
	public String getName()
	{
		return name;
	}
	public String getData()
	{
		return data;
	}
	public String getPath()
	{
		return path;
	}
	@Override
	public int compareTo(Option o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase()); 
		else 
			throw new IllegalArgumentException();
	}
	public boolean isFolder() {
		return folder;
	}
	public boolean isParent() {
		return parent;
	}
	public boolean isBack() {
		return back;
	}
	public void setBack(boolean back) {
		this.back = back;
	}
}
